package core;

import logging.Log;
import map.BotClientMapLoader;
import map.Map;
import rrt.PathPlanning;
import state_machine.StateMachine;
import control.Control;
import data_collection.DataCollection;

public class Overlord extends Thread {

    BotClientSingleton bc;
	DataCollection dc;
	StateEstimator se;
	StateMachine sm;
	PathPlanning pp;
	Control c;

	Log l;
	private long startTime;
	private Map map;

	public Overlord() {
		bc = BotClientSingleton.getInstance();
		//bc.pendOnStartup();
		map = Map.getInstance();
		map.setMap(BotClientMapLoader.loadMap());
		BotClientMapLoader.setReactorNormVectors(map);
		
		//map.setMap(ParseMap.parseFile("cheat_map.txt"));
		//m.setMap(ParseMap.parseFile("construction_map_2013.txt"));
        //m.setMap(ParseMap.parseFile("challenge_2013.txt"));

		try {Thread.sleep(2000);} catch (InterruptedException e) {}
		
		dc = DataCollection.getInstance();
		se = StateEstimator.getInstance();
		se.numBlocksLeft = map.getBlocks().size();
		sm = StateMachine.getInstance();
		pp = PathPlanning.getInstance();
		c = Control.getInstance();
		
		try {Thread.sleep(2000);} catch (InterruptedException e) {}
		
		l = Log.getInstance();
	}
 
	public void start() {
		while (true) {
			//Log.log("justed updated");
			startTime = System.currentTimeMillis();
			
			dc.step();
			se.step();
			sm.step();

			map.update();
			
			pp.step();
			//Log.log(se.getCaptureStatus());
			c.step();
			
			l.updatePose();
			try {Thread.sleep(50-(System.currentTimeMillis()-startTime));} catch (Exception e){};
		}
	}

	public static void main(String[] args) {
		System.out.println("Main");

		Overlord me = new Overlord();
		System.out.println("About to start");
		me.start();
	}
}
