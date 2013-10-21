package coms.obir;

import java.util.ArrayList;
import java.util.List;

public class CrawledImages {
	private List<String> images;

	public CrawledImages() {
		images = new ArrayList<String>();
	}

	public List<String> getImages() {
		return images;
	}

	public boolean add(String url) {
		return images.add(url);
	}

	public int size() {
		return images.size();
	}

	public String listToString() {
		String s = "";
		for (String img : this.images) {
			s += "no." + images.indexOf(img) + ":  " + img + "\n";
		}
		return s;
	}
}
