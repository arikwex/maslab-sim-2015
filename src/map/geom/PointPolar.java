package map.geom;

public class PointPolar {
	public double r, theta;
	
	public PointPolar(double r, double theta) {
		this.r = r;
		this.theta = theta;
	}
	
	public String toString() {
		return "(r: " + this.r + ", th: " + this.theta + ")";
	}
}
