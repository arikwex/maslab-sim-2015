package core;

import java.util.ArrayList;

import orc.Orc;

public class DataCollection {
    private Orc orc;

    public EncoderPair encoders;
    public ArrayList<Sonar> sonars;
    public Vision vision;
    public Delta delta;
    public ArrayList<Block> BlocksInVision; 
    
    public DataCollection(Orc orc) {
        this.orc = orc;    

        encoders = new EncoderPair(orc);
        //sonars = new ArrayList<Sonar>;
        //vision = new Vision();
        //delta = new Delta();
        //BlocksInVision = new ArrayList<Block>;
    }
    
    public void step() {
        encoders.sample();
    }
}
