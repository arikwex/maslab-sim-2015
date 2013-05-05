package map;

import java.awt.geom.Point2D;

public class Point extends Point2D {
    public double x;
    public double y;

    public Point(double[] coords) {
        this(coords[0], coords[1]);
    }
    
    public Point(double x, double y){
    	this.x = x;
    	this.y = y;
    }
    
    public Point(){
    }
    
    public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;	
	}
	
	public double angleTo(Point p) {
        return Math.atan2(p.y-this.y, p.x-this.x);
	}
	
	public Point getRotated(double theta) {
		double newX = x - Math.sin(theta) * y;
		double newY = y + Math.cos(theta) * x;
		return new Point(newX, newY);
	}

	public String toString() {
		return "(" + round(x,2) + " , " + round(y,2) + ")";
	}
	
	protected double round(double v, int sigfig) {
		return Math.round(v*Math.pow(10, sigfig))/Math.pow(10, sigfig);
	}
	
}
