package core;

import java.io.File;

import logging.Log;
import map.MapLoader;
import map.Map;
import rrt.PathPlanning;
import state_machine.StateMachine;
import control.Control;
import data_collection.DataCollection;

public class Overlord extends Thread {

	DataCollection dc;
	StateEstimator se;
	StateMachine sm;
	PathPlanning pp;
	Control c;

	Log l;
	private static long startTime;
	private Map map;

	public Overlord(String mapName) {
		map = Map.getInstance();
		map = Map.getInstance();
		MapLoader.load(map, new File("gameMaps/" + mapName));

		try {Thread.sleep(50);} catch (InterruptedException e) {}
		
		dc = DataCollection.getInstance();
		se = StateEstimator.getInstance();
		sm = StateMachine.getInstance();
		pp = PathPlanning.getInstance();
		c = Control.getInstance();
		
		try {Thread.sleep(50);} catch (InterruptedException e) {}
		
		l = Log.getInstance();
	}
 
	public void start() {
		startTime = System.currentTimeMillis();
		pp.start();
		long loopStart;
		while (timeRemaining() > 0) {
			loopStart = System.currentTimeMillis();
			dc.step();
			se.step();
			sm.step();
			pp.step();
			c.step();
			l.updatePose();
			
			try {Thread.sleep(5-(System.currentTimeMillis()-loopStart));} catch (Exception e){};
		}
		pp.end();
	}
	
	public static long timeRemaining() {
		return (3 * 60 * 1000 - (System.currentTimeMillis() - startTime));
	}

	public static void main(String[] args) {
		System.out.println("Main");

		Overlord me = new Overlord("practice_field.txt");
		System.out.println("About to start");
		me.start();
	}
}
