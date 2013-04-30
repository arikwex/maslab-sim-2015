package core;

import logging.Log;
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


    public Overlord() {
        orcControl = new OrcController(new int[]{0,1});
        orc = Orc.makeOrc();

        dc = DataCollection.getInstance();
        se = StateEstimator.getInstance();
        sm = StateMachine.getInstance();
        pp = PathPlanning.getInstance();
        c = Control.getInstance();
        
        l = Log.getInstance();
    }

    public void start() {
        while (true) {
            dc.step();
            
            dc.log();
            se.step();
            l.updatePose();
            //sm.step();
            //pp.step();
            //c.step();

        }
    }

    
    public static void main(String[] args) {
        System.out.println("compile!");
        Overlord me = new Overlord();
        me.start();
    }
}
