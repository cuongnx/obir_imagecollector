package coms.obir.vocimagecrawler;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.google.gson.Gson;
import coms.obir.Helpers;

public class VOCImageCrawler {
	private List<String> images;
	private String[] keywords;
	private Map<String, ImageAnnotation> imageDescription;
	private Map<String, double[]> featureSpace;

	private Connection dbConn;

	public String[] getKeywords() {
		return this.keywords;
	}

	public List<String> getImages() {
		return this.images;
	}

	public List<String> getCrawledImages() {
		return this.images;
	}

	public Map<String, ImageAnnotation> getImageDescription() {
		return imageDescription;
	}

	public Map<String, double[]> getFeatureSpace() {
		return featureSpace;
	}

	public VOCImageCrawler() {
		// open database
		try {
			dbConn = Helpers.openDbConnection();

			// if database is opened
			if (dbConn != null) {

				initKeywords();
				initImages();

				// update to database if parameter is true
				calculateFeatureSpace(true);

				// close database
				try {
					Helpers.closeDbConnection(dbConn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void initKeywords() throws SQLException {
		List<String> k = new ArrayList<String>();

		// insert keywords to database
		Statement st = null;
		String q = "SELECT `word` FROM `obir`.`keywords` ORDER BY `word` ASC";
		// get keywords from db
		st = dbConn.createStatement();
		ResultSet rs = st.executeQuery(q);

		while (rs.next()) {
			k.add(rs.getString(1));
		}

		// then store in array keywords
		keywords = k.toArray(new String[k.size()]);

		if (st != null) {
			st.close();
		}
	}

	private void initImages() {
		images = new ArrayList<String>();
		imageDescription = new HashMap<String, ImageAnnotation>();

		// get list of image files
		String psep = File.separator;
		File folder = new File("VOC2012" + psep + "JPEGImages");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {

				// store image name
				String fn = listOfFiles[i].getName();
				images.add(fn);

				// parse image annotation
				ImageAnnotation ia = getImageAnnotation(fn);

				// place information in map
				if (ia.getObjects().size() > 0) {
					imageDescription.put(fn, ia);
				}

			}
		}

		System.out.println("Done with " + imageDescription.size() + " images");

	}

	// get image annotations
	private ImageAnnotation getImageAnnotation(String filename) {

		System.out.println("processing " + filename);

		Document doc = getDocument(filename);

		ImageAnnotation ia = new ImageAnnotation();
		List<? extends Node> nodes = null;

		// image name
		ia.setName(filename);

		// image size
		nodes = doc.selectNodes("//annotation/size/width");
		ia.setWidth(Integer.parseInt(nodes.get(0).getText()));
		nodes = doc.selectNodes("//annotation/size/height");
		ia.setHeight(Integer.parseInt(nodes.get(0).getText()));

		// image objects
		ia.setObjects(getImageObjects(doc));

		return ia;
	}

	// get document object from XML file
	private Document getDocument(String filename) {
		String psep = File.separator;
		String name = filename.substring(0, filename.lastIndexOf("."));

		Document document = null;
		SAXReader reader = new SAXReader();
		try {
			document = reader.read("VOC2012" + psep + "Annotations" + psep
					+ name + ".xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return document;
	}

	// get image objects from XML document
	private List<ObjectDescriptor> getImageObjects(Document doc) {
		List<ObjectDescriptor> ol = new ArrayList<ObjectDescriptor>();

		List<? extends Node> nodes = doc.selectNodes("//annotation/filename");
		String fname = nodes.get(0).getText();
		String filename = fname.substring(0, fname.lastIndexOf("."));

		nodes = doc.selectNodes("//annotation/object");
		for (Node n : nodes) {
			String name = getNodeText(n, "name");
			String pose = getNodeText(n, "pose");
			int truncated = Helpers.parseInt(getNodeText(n, "truncated"));
			int difficult = Helpers.parseInt(getNodeText(n, "difficult"));

			int px = Helpers.parseInt(getNodeText(n, "point/x"));
			int py = Helpers.parseInt(getNodeText(n, "point/y"));
			Point point = new Point(px, py);

			int xmax = Helpers.parseInt(getNodeText(n, "bndbox/xmax"));
			int ymax = Helpers.parseInt(getNodeText(n, "bndbox/ymax"));
			int xmin = Helpers.parseInt(getNodeText(n, "bndbox/xmin"));
			int ymin = Helpers.parseInt(getNodeText(n, "bndbox/ymin"));
			Point[] bndbox = { new Point(xmin, ymin), new Point(xmax, ymax) };
			Point center = new Point((xmin + xmax) / 2, (ymin + ymax) / 2);

			ol.add(new ObjectDescriptor(filename, name, pose, truncated,
					bndbox, point, difficult, center));

			// classify(fname, name);

		}

		return ol;
	}

	// get text value of a node relative to current node
	private String getNodeText(Node cur_node, String child) {
		Node child_node = null;
		child_node = cur_node.selectSingleNode(child);
		if (child_node != null) {
			return child_node.getText();
		}

		return "";
	}

	// calculate feature space
	private void calculateFeatureSpace(boolean update) throws SQLException {
		// create feature space
		featureSpace = new HashMap<String, double[]>();

		// prepare statement to interact with database
		PreparedStatement st = null;
		String base_query = "UPDATE `obir`.`images` SET `vector`=?, `avg_distance`=? WHERE `filename`=?;";

		dbConn.setAutoCommit(false);

		st = dbConn.prepareStatement(base_query);
		for (Map.Entry<String, ImageAnnotation> entry : imageDescription
				.entrySet()) {
			System.out.println("Calculating for " + entry.getKey() + ": (has "
					+ entry.getValue().getObjects().size() + " objects)");

			// calculate feature vector of image based on its contained
			// objects
			double[] v = calcFeatureVector(entry.getValue(), keywords);
			double avg_dist = v[v.length - 1];
			v = Arrays.copyOf(v, v.length - 1);

			featureSpace.put(entry.getKey(), v);

			Gson gs = new Gson();
			String js_v = gs.toJson(v);

			if (update) {
				st.setString(1, js_v);
				st.setDouble(2, avg_dist);
				st.setString(3, entry.getKey());
				st.executeUpdate();
			}
		}

		dbConn.commit();
		dbConn.setAutoCommit(true);

		if (st != null) {
			st.close();
		}
	}

	private double[] calcFeatureVector(ImageAnnotation ann, String[] keywords) {

		// feature vector initialized with 0
		double[] v = new double[keywords.length + 1];

		List<String> k = Arrays.asList(keywords);

		// object area
		double[] obArea = new double[keywords.length];

		// initialize feature vector and objects' area
		for (int i = 0; i < keywords.length; ++i) {
			v[i] = 0;
			obArea[i] = 0;
		}

		// image area
		double imgArea = (double) ann.getHeight() * (double) ann.getWidth();

		// images' objects
		ObjectDescriptor[] objects = ann.getObjects().toArray(
				new ObjectDescriptor[0]);

		// average object distance
		double avg_dist = 0;
		double max_dist = 0;
		double obj_num = (double) objects.length;

		for (int i = 0; i < obj_num; ++i) {
			// object area
			ObjectDescriptor ob = objects[i];
			double oa = (double) Math
					.abs((ob.getBndbox()[0].x - ob.getBndbox()[1].x)
							* (ob.getBndbox()[0].y - ob.getBndbox()[1].y));

			obArea[k.indexOf(ob.getName())] += oa;
			ob.setArea(obArea[k.indexOf(ob.getName())]);

			for (int j = i + 1; j < obj_num; ++j) {
				Point icenter = ob.getCenter();
				Point jcenter = objects[j].getCenter();

				double dist = Math.hypot((double) (jcenter.x - icenter.x),
						(double) (jcenter.y - icenter.y));
				avg_dist += dist;
				if (dist > max_dist) {
					max_dist = dist;
				}
			}
		}

		if ((obj_num > 1) && (max_dist != 0)) {
			avg_dist = avg_dist / (obj_num * (obj_num - 1) / 2) / max_dist;
		} else {
			avg_dist = 1;
		}

		// calculate vector's elements
		for (ObjectDescriptor ob : ann.getObjects()) {
			double ratio = 1;
			int idx = k.indexOf(ob.getName());
			if (obArea[idx] < imgArea) {
				ratio = obArea[idx] / imgArea;
			}
			ob.setRatio(ratio);
			// ratio = Math.pow(ratio, 0.4);
			// v[idx] = (-Math.E * ratio * Math.log(ratio));
			v[idx] = ratio;
			ob.setWeight(v[idx]);
		}

		System.out.println("\t" + Helpers.array2string(v));
		System.out.print("\t max distance " + max_dist);
		System.out.println("\t average distance " + avg_dist);

		v[v.length - 1] = avg_dist;
		return v;
	}

	private void classify(String fname, String obname) {
		String psep = File.separator;
		Path source = Paths.get("VOC2012" + psep + "JPEGImages" + psep + fname);
		Path dest = Paths.get("classified" + psep + obname + psep + fname);

		try {
			Files.copy(source, dest, REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
