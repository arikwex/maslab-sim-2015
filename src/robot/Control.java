package robot

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
}
