package robot

public class Robot {
    
    OrcController orc;

    DataCollection dc;
    StateEstimation se;
    StateMachine sm;
    PathPlanning pp;
    Control c;


    public Robot() {
        orc = new OrcController(new int[]{0,1});

        dc = new DataCollection(orc);
        se = new StateEstimation(dc);
        sm = new StateMachine(se);
        pp = new PathPlanning(sm, se);
        c = new Control(cm, orc);
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
