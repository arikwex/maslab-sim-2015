package control;

import uORCInterface.OrcController;
import core.Config;
import data_collection.EncoderPair;

public class WheelVelocityController {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    private int wheel;
    private EncoderPair enc;
    
    private double targetVel;
    private int currPWM;
    private int prevTime;
    
    private static final int SLEW_LIM = 255;
    private PID pid;
    
    private OrcController orc;
    
    
    public WheelVelocityController(OrcController orc, int wheel) {
        this.orc = orc;
        this.wheel = wheel;
        this.pid = new PID(0, 0, 0, 0, 0);
        pid.start(0, 0);
    }
    
    public void setVelocity(double v) {
        targetVel = v;
        pid.setTarget(targetVel);
    }
    
    public void step() {
        int targetPwm = computeTargetPWM();
        orc.motorSet(wheel, targetPwm);
    }

    private int computeTargetPWM() {
        double ff = 255 * targetVel/Config.MAX_VELOCITY;
        
        double actual = Config.METERS_PER_TICK;
        if (wheel == LEFT)
            actual *= enc.dLeft / enc.dt;
        else
            actual *= enc.dRight / enc.dt;
        
        return (int)Math.round(pid.step(actual) + ff);
    }
}
