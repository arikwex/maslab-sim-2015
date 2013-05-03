package data_collection;

import java.util.ArrayList;

import logging.Log;

import core.Block;
import core.Delta;

import firmware_interfaces.SonarInterface;

import orc.Orc;
import Vision.ObjectPositionDetect;

public class DataCollection {

    private static DataCollection instance;
    
    private EncoderPair encoders;
    private SonarInterface sonarInterface;
    private ObjectPositionDetect vision;
    
    private ArrayList<Sonar> sonars;
    private Delta delta;
    private ArrayList<Block> blocksInVision; 
    
    
    private DataCollection() {
        Orc orc = Orc.makeOrc();
        
        encoders = new EncoderPair(orc);
        //sonarInterface = new SonarInterface();
        //sonars = sonarInterface.getSonars();

        vision = new ObjectPositionDetect();

        //vision = new Vision();
        delta = new Delta();
        blocksInVision = new ArrayList<Block>();
    }
    
    public static DataCollection getInstance() {
    	if (instance == null)
            instance = new DataCollection();
        return instance;   
    }
    
    public void step() {
        encoders.sample();
        //sonarInterface.sample();
    	vision.step();
        blocksInVision = vision.blocks;
    }
    
    public EncoderPair getEncoders() {
        return encoders;
    }
/*    
    public ArrayList<Sonar> getSonars() {
        return sonars;
    }
*/  
    public ArrayList<Block> getBlocks() {
        return blocksInVision;
    }
    
    public void log() {
    	if (encoders == null){
    		Log.log("No encoders");
    		return;
    	}
    	Log.log(encoders.toString());
    }
    
}
