package map;

public class Pose extends Point{
    public double theta;
    
    public Pose(double x, double y, double theta) {
        super(x, y);
        this.theta = theta;
    }
    
    public Pose clone() {
        return new Pose(x, y, theta);
    }
    
    public String toString() {
        return "(" + round(x,2) + ", " + round(y,2) + ", " + round(theta, 2) + ")";
    }
}
