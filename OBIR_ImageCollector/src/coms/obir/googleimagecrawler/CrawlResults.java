package coms.obir.googleimagecrawler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import coms.obir.CrawledImages;

public class CrawlResults implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Result> items;

	public CrawlResults() {
		items = new ArrayList<Result>();
	}

	public void setItems(List<Result> searchImages) {
		this.items = searchImages;
	}

	public List<Result> getItems() {
		return this.items;
	}

	static class Result {
		private String link;
		private String mime;
		private String fileFormat;
		private ImageURL image;

		public Result() {

		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getMime() {
			return mime;
		}

		public void setMime(String mime) {
			this.mime = mime;
		}

		public String getFileFormat() {
			return fileFormat;
		}

		public void setFileFormat(String fileFormat) {
			this.fileFormat = fileFormat;
		}

		public void setImage(ImageURL image) {
			this.image = image;
		}

		public ImageURL getImage() {
			return this.image;
		}

	}

	static class ImageURL {
		private String thumbnailLink;

		public String getThumbnailLink() {
			return this.thumbnailLink;
		}

		public void setThumbnailLink(String link) {
			this.thumbnailLink = link;
		}
	}

	public CrawledImages getCrawledImages() {
		CrawledImages s = new CrawledImages();
		for (Result img : this.items) {
			s.add(img.image.thumbnailLink);
		}
		return s;
	}

	public void appendResults(CrawlResults results) {
		this.items.addAll(results.getItems());
	}

}
