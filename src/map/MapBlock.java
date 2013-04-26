package map;

import core.Block;
import core.Config.BlockColor;


public class MapBlock extends Point {
	
	private BlockColor color;

	public MapBlock (double x, double y, BlockColor color){
		super(x,y);
		this.color = color;
	}
	public MapBlock(Block b){
		super(b.getX(),b.getY());
		this.color = b.getColor();
	}
	
	public BlockColor getColor(){
		return this.color;
	}

}
