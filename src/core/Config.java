package core;

import java.awt.Color;

import map.geom.Point;

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

    public static final double WHEELBASE = 0.213995;
    public static final double WHEEL_RADIUS = 0.0492125;
    public static final double WHEEL_CIRCUMFERENCE = WHEEL_RADIUS * Math.PI * 2.0;
    public static final double MAX_VELOCITY = WHEEL_CIRCUMFERENCE * 5.833;

    public static final double TOOCLOSE = 0.1;

	public static final int BIN_CAPACITY = 5;
	public static final double CLOSE_ENOUGH = 0.5;

    public static final double MAXLENGTH = 1.5;

    public static final String COMMENT = "#";
    public static final String SECTION_START = "{";
    public static final String SECTION_END = "}";
    
    public static double[][] ASSEMBLY_CROSS = new double[][] {{0,0,-1}, {-6,-6,-1}, {0,-6,-1}, {6,-6,-1}, {0,-12,-1}};
    public static double[][] ASSEMBLY_PYRAMID = new double[][] {{0,0,-1}, {-2.5, -6, -1}, {2.5, -6, -1}, {0,-3.5, 4}, {0, -3.5, 9}};
	
    public static double[][] botPoly = new double[][] {
    	{ -0.2, -0.1 },
    	{ 0.1, -0.1 },
    	{ 0.1, 0.1 },
    	{ -0.2, 0.1 }
    };

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

	public static final double COLLISION_MARGIN = 0.1;

    public static final int MOTOR_LEFT_DIR_PIN = 8;
	public static final int MOTOR_LEFT_PWM_PIN = 7;
    public static final int MOTOR_RIGHT_DIR_PIN = 10;
	public static final int MOTOR_RIGHT_PWM_PIN = 9;
	public static final int SERVO_GRIP_PIN = 12;
	public static final int SERVO_ELEVATION_PIN = 11;
	public static final int BALL_LAUNCHER_PIN = 37;
	public static final int RANGE_SENSOR_PIN = 36;
	public static final int ENCODER_LEFT_PIN_A = 29;
	public static final int ENCODER_LEFT_PIN_B = 30;
	public static final int ENCODER_RIGHT_PIN_A = 31;
	public static final int ENCODER_RIGHT_PIN_B = 32;

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
    
    public static final String BOTCLIENT_HOST = "18.150.7.174:6667";
    public static final String BOTCLIENT_TOKEN = "1221";
    
    public static final double METERS_PER_INCH = 0.0254;
}
