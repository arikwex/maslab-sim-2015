package core;

public class Sonar {
    public final double x;
    public final double y;
    public final double theta;
    
    public double meas;
    public long time;
    public IIRFilter filter;
    
    public Sonar(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        filter = new IIRFilter(new double[] {.1, .1, .1}, new double[] {.7});
    }
    
    public Sonar(double[] positions){
    	this.x = positions[0];
    	this.y = positions[1];
    	this.theta = positions[2];
    }
    
    public void setMeasurement(double meas, long time) {
        this.time = time;
        this.meas = meas;
    }
}