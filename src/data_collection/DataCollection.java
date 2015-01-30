package data_collection;

import hardware.Hardware;

public class DataCollection {

    private static DataCollection instance;
    
    private Hardware hw;    
    
    private DataCollection() {
        hw = Hardware.getInstance();
    }
    
    public static DataCollection getInstance() {
    	if (instance == null)
            instance = new DataCollection();
        return instance;   
    }

    public void step() {        
    	//sample here
    	hw.encoderLeft.sample();
    	hw.encoderRight.sample();
    }
}
