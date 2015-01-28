package map.geom;

import hardware.Hardware;

import java.awt.Color;
import java.util.ArrayList;

import map.Pose;
import map.elements.Stack;
import core.Config;

public class Robot extends Polygon {
    public Pose pose;
    public Stack gripping = null;
    
    private double maxRadius = 0;
    private double minRadius = Double.POSITIVE_INFINITY;

    public Robot(double x, double y, double theta) {
        super();
        this.pose = new Pose(x, y, theta);
        this.color = Color.red;
        Point p;
        for (int i = 0; i<Config.botPoly.length; i++) {
        	p = new Point(Config.botPoly[i][0], Config.botPoly[i][1]);
            this.addVertex(p);
        }
        this.close();
    }
    
    public Point getGripPoint() {
    	double arm = Math.cos(Hardware.getInstance().servoElevation.getTransientValue() * Math.PI/2) * 0.18;
    	return new Point(
    		pose.x + Math.cos(pose.theta) * arm,
    		pose.y + Math.sin(pose.theta) * arm
    	);
    }

    public void scale(double ratio) {

    }

    public void rotate(double theta) {

    }

    public Point getAbsolute(double x, double y) {
        double r = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        double phi = Math.atan(x / y);

        x = pose.x + r * Math.cos(phi + pose.theta);
        y = pose.y + r * Math.sin(phi + pose.theta);

        return new Point(x, y);
    }
    
    public double getMaxRadius() {
    	if (maxRadius == 0) {
    		maxRadius = 0;
    		Point orig = new Point(0,0);
    		for (Point p : points) {
    			if (p.distance(orig) > maxRadius)
    				maxRadius = p.distance(orig);
    		}
    	}
    	
    	return maxRadius;
    }
    
    public double getMinRadius() {
    	if (minRadius == Double.POSITIVE_INFINITY) {
    		Point orig = new Point(0,0);
    		for (Point p : points) {
    			if (p.distance(orig) < minRadius)
    				minRadius = p.distance(orig);
    		}
    	}
    	
    	//return .29;
    	return minRadius;
    }
    
    public Polygon getRotated(double theta) {
    	Polygon rotated = new Polygon();
    	for (Point p : points)
    		rotated.addVertex(p.getRotated(theta));
    	
    	rotated.close();
    	return rotated;
    }
    
    public ArrayList<Point> rotatedPoints(double start, double end, Point location) {
        ArrayList<Point> rotatedPoints = new ArrayList<Point>();
        
        double diff = end - start;
        
        if (diff > Math.PI)
            diff -= Math.PI*2;
        else if (diff < -Math.PI)
            diff += Math.PI*2;
        
        double steps = Math.ceil(Math.abs(diff)/(Math.PI/32))-1;
        
        for (int i = 0; i < steps; i++) {
            start += diff/steps;
            for (Point p : getVertices())
                rotatedPoints.add(p.getRotated(start).getTranslated(location));
        }
        
        return rotatedPoints;   
    }
}
