package vision;

public class Block implements Comparable<Block> {
	
	public enum Color{
		Red, Green;
	}
	
	private Color color;
	private double x, y, width, height;
	private double area;
	
	public Block(Color c, double x, double y, double w, double h, double a) {
		this.color = c;
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.area = a;
	}

	public Color getColor() {
		return color;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getArea() {
		return area;
	}

	@Override
	public int compareTo(Block o) {
		double myBottom = this.getY() + this.getHeight();
		double oBottom=  o.getY() + o.getHeight();
		if (oBottom > myBottom) {
			return 1;
		} else if (oBottom < myBottom) {
			return -1;
		} else {
			return 0;
		}
	}
}
