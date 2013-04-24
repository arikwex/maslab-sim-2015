package map;

import core.Config.Color;


public class MapBlock extends Point {
	
	private Color color;

	public MapBlock (double x, double y, Color color){
		super(x,y);
		this.color = color;
	}
	
	public Color getColor(){
		return this.color;
	}

}
