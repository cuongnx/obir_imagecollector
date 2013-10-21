package coms.obir.vocimagecrawler;

import java.util.List;

public class ImageAnnotation {
	private String name;
	private int width;
	private int height;
	private double area;
	private List<ObjectDescriptor> objects;

	public ImageAnnotation(String name, int width, int height,
			List<ObjectDescriptor> objects) {
		super();
		this.name = name;
		this.width = width;
		this.height = height;
		this.objects = objects;
	}

	public ImageAnnotation() {
		this.name = "";
		this.width = 0;
		this.height = 0;
		this.objects = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public List<ObjectDescriptor> getObjects() {
		return objects;
	}

	public void setObjects(List<ObjectDescriptor> objects) {
		this.objects = objects;
	}

	public double getArea() {
		return (double) this.getWidth() * (double) this.getHeight();
	}

}
