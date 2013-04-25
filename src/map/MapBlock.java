package map;

import core.Block;
import core.Config.Color;


public class MapBlock extends Point {
	
	private Color color;

	public MapBlock (double x, double y, Color color){
		super(x,y);
		this.color = color;
	}
	public MapBlock(Block b){
		super(b.getX(),b.getY());
		this.color = b.getColor();
	}
	
	public Color getColor(){
		return this.color;
	}

}
