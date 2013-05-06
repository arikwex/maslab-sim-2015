package core;

import firmware_interfaces.DeltaInterface;

public class Delta {
    private static Delta instance;
    
    private DeltaInterface di;
    
    private long[] position = new long[3];
    
    
    private int heldBlock = 0;
    private boolean pneumaticOut = false;
    private boolean midMove = false;
    private boolean isZeroed = false;
    
    private Delta() {
        //di = new DeltaInterface();
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
        
    }

	public void step() {
		
	}
	
	public void move(int[] steps) {
	    midMove = true;
	    DeltaInterface.move(steps);
	    
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
}