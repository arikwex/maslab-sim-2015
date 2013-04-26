package core;

import java.util.ArrayList;

import firmware_interfaces.SonarInterface;

import orc.Orc;

public class DataCollection {

    public EncoderPair encoders;
    public SonarInterface sonarInterface;
    public ArrayList<Sonar> sonars;
    public Vision vision;
    public Delta delta;
    public ArrayList<Block> BlocksInVision; 
    
    public DataCollection(Orc orc) {
        
        encoders = new EncoderPair(orc);
        sonarInterface = new SonarInterface();
        sonars = sonarInterface.getSonars();


        //vision = new Vision();
        //delta = new Delta();
        //BlocksInVision = new ArrayList<Block>;
    }
    
    public void step() {
        encoders.sample();
        sonarInterface.sample();
    }
}
