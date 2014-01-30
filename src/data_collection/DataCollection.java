package data_collection;

import hardware.Hardware;

import java.util.ArrayList;

import core.Block;
import core.Config;

public class DataCollection {

    private static DataCollection instance;
    
    private Hardware hw;
    
    private ArrayList<Block> blocksInVision;
    
    private boolean[] digitalIn;
    
    
    private DataCollection() {
        hw = Hardware.getInstance();
        
        digitalIn = new boolean[1];

        blocksInVision = new ArrayList<Block>();
    }
    
    public static DataCollection getInstance() {
    	if (instance == null)
            instance = new DataCollection();
        return instance;   
    }

    public void step() {        
        sampleDigitalPins();
        
        //sonarInterface.sample();
        
        // TODO: Add vision processing step here
    	//vision.step();
        //blocksInVision = vision.blocks;
    }
    
    private void sampleDigitalPins() {
        digitalIn[0] = hw.rangeSensor.getValue();
    }

    public ArrayList<Block> getBlocks() {
        return blocksInVision;
    }
    
    public boolean[] getDigitalPins() {
        return digitalIn;
    }
}
