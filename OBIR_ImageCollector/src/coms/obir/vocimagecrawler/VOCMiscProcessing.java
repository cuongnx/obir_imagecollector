package coms.obir.vocimagecrawler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class VOCMiscProcessing {
	private List<String> images;
	private Map<String, ImageAnnotation> imageDescription;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new VOCMiscProcessing()).run();
	}

	public void run() {
		VOCImageCrawler c = new VOCImageCrawler();

		// get image annotations
		imageDescription = c.getImageDescription();

		String psep = File.separator;
		File folder = new File("VOC2012" + psep + "JPEGImages");
		File[] listOfFiles = folder.listFiles();
		Color[] colors = { Color.decode("#00FF00"), Color.decode("#0101DF"),
				Color.decode("#FF0040"), Color.decode("#B45F04"),
				Color.decode("#C725CC") };
		Random rand = new Random();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {

				// store image name
				String fn = listOfFiles[i].getName();

				BufferedImage img = null;
				try {
					img = ImageIO.read(listOfFiles[i]);
					ImageAnnotation ann = imageDescription.get(fn);
					List<ObjectDescriptor> obList = ann.getObjects();

					System.out.print("drawing " + fn);

					Graphics g = img.createGraphics();

					for (ObjectDescriptor ob : obList) {
						int xmin = ob.getBndbox()[0].x;
						int ymin = ob.getBndbox()[0].y;
						int xmax = ob.getBndbox()[1].x;
						int ymax = ob.getBndbox()[1].y;

						g.setColor(colors[rand.nextInt(5)]);
						g.drawRect(xmin, ymin, xmax - xmin, ymax - ymin);
						g.drawLine(ob.getCenter().x, ob.getCenter().y,
								ob.getCenter().x + 5, ob.getCenter().y + 5);

						System.out.format(" (%d,%d,%d,%d) ", xmin, ymin, xmax,
								ymax);
					}

					System.out.println("");

					File outputfile = new File("bbox" + psep + fn);
					ImageIO.write(img, "jpg", outputfile);

				} catch (IOException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
