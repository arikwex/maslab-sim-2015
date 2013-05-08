package core;

import java.awt.Color;

import map.Point;

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

	public static final int BIN_CAPACITY = 5;
	public static final double CLOSE_ENOUGH = 0.5;

    public static final double MAXLENGTH = 1.5;

    public static final String COMMENT = "#";
    public static final String SECTION_START = "{";
    public static final String SECTION_END = "}";
    
    public static double[][] ASSEMBLY_CROSS = new double[][] {{0,0,-1}, {-6,-6,-1}, {0,-6,-1}, {6,-6,-1}, {0,-12,-1}};
    public static double[][] ASSEMBLY_PYRAMID = new double[][] {{0,0,-1}, {-2.5, -6, -1}, {2.5, -6, -1}, {0,-3.5, 4}, {0, -3.5, 9}};
	
    public static double[][] botPoly = new double[][] {{ -.34, -.27 }, { -.34, .27 }, { .22, .27 }, {.34, 0}, { .22, -.27 }};
    //public static double[][] botPoly = new double[][] {{ -.32, -.25 }, { -.32, .25 }, { .20, .25 }, { .20, -.25 }};
    //public static double[][] botPoly = new double[][] {{ -.37, -.23 }, { -.37, .23 }, { .11, .23 }, { .11, -.23 }};
    //public static double[][] botPoly = new double[][] {{ -.30, -.23 }, { -.30, .23 }, { .18, .23 }, { .18, -.23 }};
    //public static double[][] botPoly = new double[][] {{ -.3, -.2 }, { -.3, .2 }, { .2, 0 }};
    //public static double[][] botPoly = new double[][] {{ -.32, -.22 }, { -.32, .22 }, { .12, .22 }, { .12, -.22 }};

    //constants for distance vs sizeP 
	public static int c;
	public static int m; 
	public static int CANVAS_CENTER;
	public static final double fieldOfViewHoriz = 0.8796; // in radians
	public static final double fieldOfViewVert = 0.6597; // in radians
	public static final double camHeight = 0.56;
	public static final double CAMYMAXDIST = 1.26;
	public static final double CAMYDIST = 1.15;
	public static final double CAMYFROMBOT = 21;
	public static final double CAMYQUAD = 0.001797;// to cm
	public static final double CAMYLIN = -0.01893;// to cm
	public static final double CAMYC = 38.07;// to cm
	public static final double CAMXLIN = -0.25904;// to cm
	public static final double CAMXC = 1.36346;// to cm
	
	//+
	public static final double CAMXDIST = 0.48;
	
	public static final long ASSEMBLY_TOO_LONG = 60000;
	public static final long COLLECT_TOO_LONG = 600000;
	public static final long EXPLORE_TOO_LONG = 60000;
	public static final long CHALLENGE_TIME = 600000;

	public static final double ROBOT_RADIUS = 0.2;

	public static final double COLLISION_MARGIN = -0.00;
	
	public static final int ONE_BLOCK_PIN = 6;
	public static final int TWO_BLOCK_PIN = 7;

	public static final String DELTA_PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyUSB0", // Linux
        "COM14", // Windows
    };
	public static final double DELTA_CM_PER_STEP = 0.02673181188;
	public static final double DELTA_STEPS_PER_CM = 37.4086127846;
    public static final long DELTA_STEP_MAX = (long)(20*DELTA_STEPS_PER_CM);
    public static final double DELTA_MICROSTEPS_PER_CM = DELTA_STEPS_PER_CM*8;
    public static final double DELTA_LINK_LENGTH = 34.29;
    public static final double DELTA_SIDE = 25.654;
    public static final double DELTA_ZERO_OFFSET = -(70.6 - 7 - 5); //height to top pivot - height to end effector pivot - block
	public static final String CAMERA_CONFIG_FILE = "camera_config.txt";
    public static final Point[] DELTA_POSITION = {new Point(-7.5,-4.33),new Point(7.5,-4.33),new Point(0,8.0829)};

    public static final double[] DELTA_TOP_OUT = new double[] {0,0,-1.5 * DELTA_ZERO_OFFSET};
    
    
	public static final int PIXELHEIGHT = 240;

	public static final int PIXELWIDTH = 320;

    public static final double RRT_GOAL_BIAS = .05;
}
