package map;

import java.awt.Color;

import core.Config;
import core.Config.BlockColor;

public class MapBlock extends Point {

    private BlockColor color;
	private int size;

    public MapBlock(Point p, BlockColor color) {
        this(p.x, p.y, color);
    }

    public MapBlock(double x, double y, BlockColor color) {
        super(x, y);
        this.color = color;
    	this.size = 1;
    }

    public MapBlock() {
    	super();
    	this.color = BlockColor.NONE;
    	this.size = 1;
	}

	public BlockColor getColor() {
        return this.color;
    }

	public void setPoint(Point p) {
		this.x = p.x;
		this.y = p.y;
		
	}

	public void setColor(Color color) {
		this.color = Config.ColorToBlockColor(color);
		
	}

	public void setSize(int size) {
		this.size = size;
		
	}

}
