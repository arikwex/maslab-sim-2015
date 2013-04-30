package data_collection;

import java.util.ArrayList;

import logging.Log;

import core.Block;
import core.Delta;

import firmware_interfaces.SonarInterface;

import orc.Orc;

public class DataCollection {

    private static DataCollection instance;
    
    public EncoderPair encoders;
    public SonarInterface sonarInterface;
    public ArrayList<Sonar> sonars;
    public Delta delta;
    public ArrayList<Block> BlocksInVision; 
    
    private DataCollection() {
        Orc orc = Orc.makeOrc();
        
        encoders = new EncoderPair(orc);
        //sonarInterface = new SonarInterface();
        //sonars = sonarInterface.getSonars();


        //vision = new Vision();
        //delta = new Delta();
        //BlocksInVision = new ArrayList<Block>;
    }
    
    public static DataCollection getInstance() {
        if (instance == null)
            instance = new DataCollection();
        return instance;   
    }
    
    public void step() {
        encoders.sample();
        //System.out.println(encoders);
        //sonarInterface.sample();
    }
    
    public void log() {
        Log.getInstance().log(encoders.toString());
    }
    
}
