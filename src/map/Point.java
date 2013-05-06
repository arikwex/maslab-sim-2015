package map;

import java.awt.geom.Point2D;

import utils.Utils;

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
	
	public Point getRotated(double t) {
		return new Point(x*Math.cos(t)-y*Math.sin(t), y*Math.cos(t) + x*Math.sin(t));
	}
	
	public Point getTranslated(Point p) {
	    return new Point(x + p.x, y + p.y);
	}

	public String toString() {
		return "(" + Utils.round(x,2) + " , " + Utils.round(y,2) + ")";
	}	
}
