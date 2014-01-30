package vision;

public class Vector2D {
	public double x,y;
	
	public Vector2D( double x, double y ) {
		this.x = x;
		this.y = y;
	}
	
	public double dot( Vector2D B ) {
		return x*B.x+y*B.y;
	}
	
	public Vector2D perp() {
		// X,Y --> Y,-X
		return new Vector2D(y,-x);
	}
	
	public double getMagnitude() {
		double m = Math.sqrt(x*x+y*y);
		return m;
	}
	
	public Vector2D normalize() {
		double m = getMagnitude();
		return new Vector2D(x/m,y/m);
	}
}
