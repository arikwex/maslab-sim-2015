package vision;

public class Ball {
	public Ball( double x, double z, boolean isRed ) {
		set(x,z);
		this.isRed = isRed;
	}
	public void set( double x, double z ) {
		this.x = x;
		this.z = z;
		this.r = Math.sqrt(x*x+z*z);
		this.theta = Math.atan(x/(z+0.0001));
	}
	public double x, z;
    public double r, theta;
    public boolean isRed;
}
