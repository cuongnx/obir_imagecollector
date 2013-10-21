package coms.obir;

import coms.obir.googleimagecrawler.*;
import coms.obir.vocimagecrawler.*;

public class ImageCollector {

	public ImageCollector() {

	}

	public static void main(String[] args) {


		ImageCollector crawler = new ImageCollector();
		
		// crawler.crawlingGoogleImage();
		crawler.crawlVOCImage();

	}

	private void crawlingGoogleImage() {
		String kw = "heart";
		String query = kw + " and star";

		GoogleImageCrawlerTest t = new GoogleImageCrawlerTest();
		t.run_test(kw,query);
	}

	private void crawlVOCImage() {
		VOCImageCrawlerTest t = new VOCImageCrawlerTest();

		String[] password = { "car","sheep" };
		//t.run_test(password);
	}

}
