package core;

import java.util.ArrayList;

import map.Point;
import firmware_interfaces.DeltaInterface;

public class Delta {
    private static Delta instance;
    
    private DeltaInterface di;
    
    private long[] position = new long[3];
    private ArrayList<int[]> moves;
    
    private int heldBlock = 0;
    private boolean pneumaticOut = false;
    private boolean midMove = false;
    private boolean isZeroed = false;
    
    public Point[] DELTA_POSITION;
    
    private Delta() {
    	double side = Config.DELTA_SIDE;
    	DELTA_POSITION = new Point[3];
    	DELTA_POSITION[0] = new Point(-side/2, side/(2*Math.sqrt(3)));
    	DELTA_POSITION[1] = new Point(0, side/Math.sqrt(3));
    	DELTA_POSITION[2] = new Point(side/2, side/(2*Math.sqrt(3)));
        System.out.println("Pos 1: " + DELTA_POSITION[0] + " Pos 2: " + DELTA_POSITION[1] + " Pos 3: " + DELTA_POSITION[2]);
        di = new DeltaInterface();
    }
    
    public static Delta getInstance() {
        if (instance == null)
            instance = new Delta();
        return instance;
    }
    
    
    public void loadData() {
        
    }
    
    public void getPosition() {
        
    }
    
    public double stepsToZ() {
        return 0.0;
    }
    
    public int[] computeSteps(double x, double y, double z) {
        return null;
    }
    
    public void goToPosition(double x, double y, double z) {
        double[] deltas = {0,0,0};
        Point pos = new Point(x,y);
        int[] steps= {0,0,0};
       
        for (int i = 0; i< deltas.length; i++){
        	double d = pos.distance(DELTA_POSITION[i]);
        	deltas[i] = Math.sqrt(Math.pow(Config.DELTA_LINK_LENGTH, 2)-Math.pow(d, 2))-63.5;  	
        }
        
        double max = getMaxValue(deltas);
        for (int i = 0; i< deltas.length; i++){
        	deltas[i] -= max;
        	steps[i] = (int) ((deltas[i])*Config.DELTA_MICROSTEPS_PER_CM);
        }
        
        System.out.println("Delta 1: " + deltas[0] + " Delta 2: " + deltas[1] + " Delta 3: " + deltas[2]);
        System.out.println("Steps 1: " + steps[0] + " Steps 2: " + steps[1] + " Steps 3: " + steps[2]);
        this.move(steps);

    }

    public static double getMaxValue(double[] numbers){
    	double maxValue = numbers[0];
    	for(int i=1;i < numbers.length;i++){
    		if(numbers[i] > maxValue){
    		  maxValue = numbers[i];
    		}
    	  }
    	return maxValue;
    	}

    public static double getMinValue(double[] numbers){
    	double minValue = numbers[0];
    	 for(int i=1;i<numbers.length;i++){
    	    if(numbers[i] < minValue){
    		  minValue = numbers[i];
    		}
    	  }
    	return minValue;
    	}
	public void step() {
		midMove = di.ready;
		if (!moves.isEmpty() && !midMove){
			this.move(moves.get(0));
			moves.remove(0);
		}
	}
	
	public void move(int[] steps) {
	    midMove = true;
	    di.move(steps);
	    
	    for (int i = 0; i < steps.length; i++) {
	        position[i] += steps[i];
	        if (position[i] < 0)
                position[i] = 0;
	        if (position[i] > Config.DELTA_STEP_MAX)
                    position[i] = Config.DELTA_STEP_MAX;
	    }
	}
	
	public void topOut() {
	    isZeroed = true;
	    move(new int[] {9999,9999,9999});
	}
	
	public void firePneumatic() {
	}
	
	public void retractPneumatic() {
	}
	
	public void collectBlock() {	    
	}
	
	public void placeBlock() {
	    
	}

	public void placeNextBlock() {
	}

	public void grabNextBlock() {
	}

	public void PutBlockInBin() {
	}
	public static void main(String[] args) throws Exception {
    	Delta main = new Delta();
        System.out.println("Started");
        Thread.sleep(3000);
        main.topOut();
        Thread.sleep(5000);
        int[] steps = {-3000,-3000,-3000};
        
        main.goToPosition(17,21,0);
        
        
 	}
}
