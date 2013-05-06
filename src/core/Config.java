package core;

import java.awt.Color;

public class Config {
    public static final double minDist = 0;

    public enum BlockColor {
        RED, GREEN, BLUE, YELLOW, NONE
    }

    public static final double blockSize = 0.05; //side of the block
    
    public static BlockColor ColorToBlockColor(Color c) {
    	if (c.equals(Color.RED)){
    		return BlockColor.RED;
    	}
    	else if (c.equals(Color.GREEN)){
    		return BlockColor.GREEN;
    	}
    	else if (c.equals(Color.BLUE)){
    		return BlockColor.BLUE;
    	}
    	else if (c.equals(Color.YELLOW)){
    		return BlockColor.YELLOW;
    	}
    	return BlockColor.NONE;
    }
    
    public static Color BlockColorToColor(BlockColor bc) {
    	if (bc.equals(BlockColor.RED)){
    		return Color.RED;
    	}
    	else if (bc.equals(BlockColor.GREEN)){
    		return Color.GREEN;
    	}
    	else if (bc.equals(BlockColor.BLUE)){
    		return Color.BLUE;
    	}
    	else if (bc.equals(BlockColor.YELLOW)){
    		return Color.YELLOW;
    	}
    	return Color.BLACK;
    }
    
    public static double[][] sonarPositions = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 },
            { 0, 0, 0 }, { 0, 0, 0 } };

    public static final double WHEELBASE = .435;
    public static final double TICKS_PER_REV = 133236;
    public static final double WHEEL_RADIUS = .0625;
    public static final double METERS_PER_TICK = (WHEEL_RADIUS * Math.PI * 2) / TICKS_PER_REV;
    public static final double MAX_VELOCITY = WHEEL_RADIUS * Math.PI * 2 * 1.183;

    public static final double TOOCLOSE = 0.1;
	public static final int BIN_CAPACITY = 50;
	public static final double CLOSE_ENOUGH = 0.5;

    public static final double MAXLENGTH = 1.5;

    public static final String COMMENT = "#";
    public static final String SECTION_START = "{";
    public static final String SECTION_END = "}";
	
    public static double[][] botPoly = new double[][] {{ -.34, -.27 }, { -.34, .27 }, { .22, .27 }, {.34, 0}, { .22, -.27 }};
    //public static double[][] botPoly = new double[][] {{ -.32, -.25 }, { -.32, .25 }, { .20, .25 }, { .20, -.25 }};
    //public static double[][] botPoly = new double[][] {{ -.37, -.23 }, { -.37, .23 }, { .11, .23 }, { .11, -.23 }};
    //public static double[][] botPoly = new double[][] {{ -.30, -.23 }, { -.30, .23 }, { .18, .23 }, { .18, -.23 }};
    //public static double[][] botPoly = new double[][] {{ -.3, -.2 }, { -.3, .2 }, { .2, 0 }};
    //public static double[][] botPoly = new double[][] {{ -.32, -.22 }, { -.32, .22 }, { .12, .22 }, { .12, -.22 }};
    
	public static final long ASSEMBLY_TOO_LONG = 60000;
	public static final long COLLECT_TOO_LONG = 60000;
	public static final long EXPLORE_TOO_LONG = 60000;
	public static final long CHALLENGE_TIME = 600000;

	public static final double ROBOT_RADIUS = 0.2;

	public static final double COLLISION_MARGIN = -0.00;
	
	public static final int ONE_BLOCK_PIN = 0;
	public static final int TWO_BLOCK_PIN = 1;

	public static final double DELTA_CM_PER_STEP = 0.02673181188;
	public static final double DELTA_STEPS_PER_CM = 37.4086127846;
    public static final long DELTA_STEP_MAX = (long)(20*DELTA_STEPS_PER_CM);
}
