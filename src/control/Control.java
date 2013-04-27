package control;

import rrt.PathPlanning;
import core.StateEstimator;
import map.Point;
import map.Robot;
import uORCInterface.OrcController;

public class Control {
    private OrcController orc;
    private PathPlanning pp;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    public Control(OrcController orc, PathPlanning pp) {
        this.orc = orc;
        this.pp = pp;
    }

    private void setMotors(int left, int right) {
        orc.motorSet(LEFT, left);
        orc.motorSet(RIGHT, right);
    }
    
    public void step() {
        Robot bot = null;
        Point wayPoint = null;
    }
    
    public void goToWaypoint(Point wp) {
        
    }
}
