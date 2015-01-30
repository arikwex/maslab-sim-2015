package map;

import map.geom.Point;
import utils.Utils;

public class Pose extends Point{
    public double theta;
    
    public Pose(double x, double y, double theta) {
        super(x, y);
        this.theta = theta;
    }
    
    public Pose(Point p, double theta) {
        super(p);
        this.theta = theta;
    }
    
    public Pose clone() {
        return new Pose(x, y, theta);
    }
    
    public String toString() {
        return "(" + Utils.round(x,2) + ", " + Utils.round(y,2) + ", " + Utils.round(theta, 2) + ")";
    }
}
