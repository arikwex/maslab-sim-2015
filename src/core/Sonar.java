package core;

public class Sonar {
    public final double x;
    public final double y;
    public final double theta;
    
    public double meas;
    public long time;
    
    public Sonar(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }
    
    public void setMeasurement(double meas, long time) {
        
    }
}