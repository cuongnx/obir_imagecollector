package coms.obir.vocimagecrawler;

import java.awt.Point;

public class BoundBox {
	private Point xmin;
	private Point ymin;
	private Point xmax;
	private Point ymax;

	public Point getXmin() {
		return xmin;
	}

	public void setXmin(Point xmin) {
		this.xmin = xmin;
	}

	public Point getYmin() {
		return ymin;
	}

	public void setYmin(Point ymin) {
		this.ymin = ymin;
	}

	public Point getXmax() {
		return xmax;
	}

	public void setXmax(Point xmax) {
		this.xmax = xmax;
	}

	public Point getYmax() {
		return ymax;
	}

	public void setYmax(Point ymax) {
		this.ymax = ymax;
	}
}
