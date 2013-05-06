package data_collection;

import java.util.ArrayList;

import logging.Log;

import core.Block;
import core.Config;
import core.Delta;

import firmware_interfaces.SonarInterface;

import orc.Orc;
import uORCInterface.OrcController;
import vision.ObjectPositionDetect;

public class DataCollection {

    private static DataCollection instance;
    
    private OrcController orc;
    
    private EncoderPair encoders;
    private SonarInterface sonarInterface;
    private ObjectPositionDetect vision;
    
    private ArrayList<Sonar> sonars;
    private Delta delta;
    private ArrayList<Block> blocksInVision;
    
    private boolean[] digitalIn;
    
    
    
    private DataCollection() {
        orc = new OrcController(new int[] {0,1});
        
        digitalIn = new boolean[8];
        
        encoders = new EncoderPair();
        //sonarInterface = new SonarInterface();
        //sonars = sonarInterface.getSonars();

        vision = ObjectPositionDetect.getInstance();

        blocksInVision = new ArrayList<Block>();
    }
    
    public static DataCollection getInstance() {
    	if (instance == null)
            instance = new DataCollection();
        return instance;   
    }

    public void step() {
        encoders.sample();
        
        sampleDigitalPins();
        
        //sonarInterface.sample();
    	//vision.step();
        blocksInVision = vision.blocks;
    }
    
    private void sampleDigitalPins() {
        digitalIn[Config.ONE_BLOCK_PIN] = orc.digitalRead(Config.ONE_BLOCK_PIN);
        digitalIn[Config.TWO_BLOCK_PIN] = orc.digitalRead(Config.TWO_BLOCK_PIN);
    }

    public ArrayList<Sonar> getSonars() {
        return sonars;
    }

    public ArrayList<Block> getBlocks() {
        return blocksInVision;
    }
    
    public boolean[] getDigitalPins() {
        return digitalIn;
    }
    
    public EncoderPair getEncoders() {
        return encoders;
    }
    
    public void log() {
    	if (encoders == null){
    		Log.log("No encoders");
    		return;
    	}
    	Log.log(encoders.toString());
    }
    
}
