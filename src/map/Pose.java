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
}
