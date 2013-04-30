package core;

import java.util.ArrayList;

import Vision.ObjectPositionDetect;

import firmware_interfaces.SonarInterface;

import orc.Orc;

public class DataCollection extends Thread {

    private EncoderPair encoders;
    private SonarInterface sonarInterface;
    private ArrayList<Sonar> sonars;
    private ObjectPositionDetect vision;
    //public Vision vision;
    public Delta delta;
    public ArrayList<Block> BlocksInVision;
	public boolean ready;
	public double dLeft;
	public double dRight; 
    
    public DataCollection(Orc orc) {
        
        encoders = new EncoderPair(orc);
        sonarInterface = new SonarInterface();
    	vision = new ObjectPositionDetect();

        //vision = new Vision();
        //delta = new Delta();
        //BlocksInVision = new ArrayList<Block>;
    }
    
    public void step() {
        sonars = sonarInterface.getSonars();
        BlocksInVision = vision.blocks;
        dLeft = encoders.dLeft;
        dRight = encoders.dRight;
    }
    
    public void run() {
    	vision.start();
    	encoders.start();
    	sonarInterface.start();
    	while (true){
    		while (!(vision.ready && encoders.ready && sonarInterface.ready)){
        		try {Thread.sleep((long) 0.0001);} catch (InterruptedException e) {}
        	}
    		step();
        	ready = true;
    	}
    }
}
