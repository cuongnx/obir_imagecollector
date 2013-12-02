package coms.obir.vocimagecrawler;

import java.awt.Point;

public class ObjectDescriptor {
	private String owner;
	private String name;
	private String pose;
	private int truncated;
	private Point[] bndbox;
	private Point point;
	private int difficult;
	private Point center;

	private double area;
	private double ratio;
	private double weight;

	public ObjectDescriptor(String owner, String name, String pose,
			int truncated, Point[] bndbox, Point point, int difficult,
			Point center) {
		super();
		this.owner = owner;
		this.name = name;
		this.pose = pose;
		this.truncated = truncated;
		this.bndbox = bndbox;
		this.point = point;
		this.difficult = difficult;
		this.center = center;

		area = 0.0;
		ratio = 0.0;
		weight = 0.0;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPose() {
		return pose;
	}

	public void setPose(String pose) {
		this.pose = pose;
	}

	public int getTruncated() {
		return truncated;
	}

	public void setTruncated(int truncated) {
		this.truncated = truncated;
	}

	public Point[] getBndbox() {
		return bndbox;
	}

	public void setBndbox(Point[] bndbox) {
		this.bndbox = bndbox;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public int getDifficult() {
		return difficult;
	}

	public void setDifficult(int difficult) {
		this.difficult = difficult;
	}
}
