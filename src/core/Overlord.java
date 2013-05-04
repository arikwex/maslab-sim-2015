package core;

import java.io.IOException;
import java.text.ParseException;

import logging.Log;
import logging.RobotGraph;
import map.Map;
import map.ParseMap;
import control.Control;
import data_collection.DataCollection;
import orc.Orc;
import rrt.PathPlanning;
import state_machine.StateMachine;
import uORCInterface.OrcController;

public class Overlord extends Thread{
    
    OrcController orcControl;
    Orc orc;

    DataCollection dc;
    StateEstimator se;
    StateMachine sm;
    PathPlanning pp;
    Control c;
    
    Log l;
	private RobotGraph f;
	private long startTime;


    public Overlord() {
    	try {
    		Map m = Map.getInstance();
			m.setMap(ParseMap.parseFile("challenge_2013.txt"));
			
			
//	        System.out.println("making stuff");
	        orcControl = new OrcController(new int[]{0,1});
//	        System.out.println("made orcController");
	        orc = Orc.makeOrc();
//	        System.out.println("made orc");
	        dc = DataCollection.getInstance();
//	        System.out.println("made DataCollection");
	        se = StateEstimator.getInstance();
	        se.numBlocksLeft = m.getBlocks().size();
//	        System.out.println("made StateEstimator");
	        sm = StateMachine.getInstance();
	        sm.goal = m.closestBlock();
//	        System.out.println("made StateMachine");
	        
	        pp = PathPlanning.getInstance();
//	        System.out.println("made PathPlanning");
	        c = Control.getInstance();
//	        System.out.println("made Control");
	        
			f = new RobotGraph(m);
//	        System.out.println("made graph");

			l = Log.getInstance(f);
//	        System.out.println("made Log");
			//startTime = System.currentTimeMillis();
        	
    	} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    public void start() {
        while (true) {    
    			startTime = System.currentTimeMillis();
            	dc.step();
//        		System.out.println("justed stepped dc");
                
                dc.log();
//        		System.out.println("justed logged dc");
                se.step();
//        		System.out.println("justed stepped se");
                l.updatePose();
//       		System.out.println("justed updated pose");

                
    			sm.step();
//      		System.out.println("justed updated sm");

    			pp.step();
//        		System.out.println("justed updated pp");
    			c.step(); 	        
        		
        		f.repaint();
        		System.out.println("Step time is "+(System.currentTimeMillis()-startTime));
    		    
        		
        }
    }

    
    public static void main(String[] args) {
        System.out.println("Main");

    	Overlord me = new Overlord();
        System.out.println("About to start");
        me.start();
    }
}
