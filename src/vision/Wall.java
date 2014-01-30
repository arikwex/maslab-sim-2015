package vision;

public class Wall {
	public Vector2D left;
	public Vector2D right;
	public Type type = Type.Wall;
	
	public enum Type {
		Wall,
		Silo,
		Reactor
	}
	
	public Wall( Vector2D left, Vector2D right, Type type ) {
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public Vector2D getNormal() {
		return (new Vector2D(right.x-left.x,right.y-left.y)).normalize();
	}
}
