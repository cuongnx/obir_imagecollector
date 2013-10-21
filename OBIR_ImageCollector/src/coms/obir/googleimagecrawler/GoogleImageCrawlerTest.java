package coms.obir.googleimagecrawler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import coms.obir.CrawledImages;
import coms.obir.SURFHelpers;

public class GoogleImageCrawlerTest {

	private static double mDistanceThreshold = 0.4;
	private static int mMatchPointNumber = 2;

	public GoogleImageCrawlerTest() {

	}

	public void run_test(String kw, String query) {
		BufferedImage image = null;
		BufferedImage template = null;
		SURFHelpers surf = new SURFHelpers();

		// get template image
		try {
			template = ImageIO.read(new File("C:\\Users\\cuongnx\\Desktop\\"
					+ kw + "_template.jpg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<SURFInterestPoint> templatePoints = surf
				.getFreeOrientedInterestPoints(template);
		System.out.println("Template image has " + templatePoints.size()
				+ " points");

		// prepare directories
		String psep = File.separator;

		File remotedir = new File("results" + psep + kw + psep
				+ "remote_images" + psep + query);
		if (!remotedir.exists()) {
			remotedir.mkdirs();
		} else {
			try {
				FileUtils.cleanDirectory(remotedir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File usabledir = new File("results" + psep + kw + psep
				+ "usable_images" + psep + query);
		if (!usabledir.exists()) {
			usabledir.mkdirs();
		} else {
			try {
				FileUtils.cleanDirectory(usabledir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		GoogleImageCrawler r = new GoogleImageCrawler(query);

		// get results from googling
		CrawledImages results = r.getSearchResults();

		for (String u : results.getImages()) {
			try {

				URL url = new URL(u);
				int i = results.getImages().indexOf(u);

				// read image and write to remote_images dir
				image = ImageIO.read(url);
				ImageIO.write(image, "png", new File(remotedir.getPath() + psep
						+ i + ".png"));

				// get image SURF interest points

				List<SURFInterestPoint> imgPoints = surf
						.getFreeOrientedInterestPoints(image);
				// matching
				Map<SURFInterestPoint, SURFInterestPoint> matched = new HashMap<SURFInterestPoint, SURFInterestPoint>();
				for (SURFInterestPoint tp : templatePoints) {
					double min = Double.MAX_VALUE;
					int minidx = -1;
					for (SURFInterestPoint ip : imgPoints) {
						double d = tp.getDistance(ip);
						if ((d < min) && (d < mDistanceThreshold)) {
							min = d;
							minidx = imgPoints.indexOf(ip);
						}
					}
					if (minidx != -1)
						matched.put(tp, imgPoints.get(minidx));
				}

				System.out.println("Image " + i + ": " + matched.size()
						+ " points matched out of " + imgPoints.size()
						+ " points");

				if (matched.size() >= mMatchPointNumber) {
					ImageIO.write(image, "png", new File(usabledir.getPath()
							+ psep + i + ".png"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("DONE!");

		/*
		 * URL url; try { url = new URL(
		 * "http://www.google.com/complete/search?output=toolbar&q=cup%20and");
		 * 
		 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		 * conn.setRequestMethod("GET"); conn.setRequestProperty("Accept",
		 * "application/json"); BufferedReader reader = new BufferedReader(new
		 * InputStreamReader( url.openStream()));
		 * 
		 * String s; while ((s = reader.readLine()) != null) {
		 * System.out.println(s); } } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

	}
}
