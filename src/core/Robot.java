package core;

import orc.Orc;
import state_machine.StateMachine;
import uORCInterface.OrcController;

public class Robot {
    
    OrcController orcControl;
    Orc orc;

    DataCollection dc;
    StateEstimator se;
    StateMachine sm;
    PathPlanning pp;
    Control c;


    public Robot() {
        orcControl = new OrcController(new int[]{0,1});
        orc = Orc.makeOrc();

        dc = new DataCollection(orc);
        se = new StateEstimator(dc);
        sm = new StateMachine(se);
        pp = new PathPlanning(sm, se);
        c = new Control(orcControl, pp);
    }

    public void start() {
        while (true) {
            dc.step();
            se.step();
            sm.step();
            pp.step();
            c.step();

        }
    }
}
