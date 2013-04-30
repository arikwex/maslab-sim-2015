package core;

import control.Control;
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


    public Overlord() {
        orcControl = new OrcController(new int[]{0,1});
        orc = Orc.makeOrc();

        //dc = new DataCollection(orc);
        se = new StateEstimator(dc);
        sm = new StateMachine(se);
        pp = new PathPlanning(sm, se);
        c = new Control(orcControl, pp);
    }

    public void run() {
    	se.start();
    	while (true){
    		while (!se.ready){
        		try {Thread.sleep((long) 0.0001);} catch (InterruptedException e) {}
        	}
    		sm.step();
            pp.step();
            c.step();
    	}            
    }
    
    public static void main(String[] args) {
        System.out.println("compile!");
        Overlord me = new Overlord();
        me.start();
    }
}
