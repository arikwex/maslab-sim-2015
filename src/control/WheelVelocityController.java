package control;

import hardware.Hardware;
import core.Config;

public class WheelVelocityController {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    private int wheel;
    
    private double targetVel = 0;
    private double currentOut = 0;
    private long prevTime;
    
    private static final int SLEW_LIM = 1000; //pwm units per second
    private PID pid;
    
    private Hardware hw;
    
    
    public WheelVelocityController(Hardware hw, int wheel) {
        this.hw = hw;
        this.wheel = wheel;
        this.pid = new PID(0, .2, 0, 1.0, 1.0);
        pid.start(0, 0);
        prevTime = System.currentTimeMillis();
    }
    
    public void setVelocity(double v) {
        targetVel = v;
        pid.setTarget(targetVel);
    }
    
    public void step() {
        double currentOut = computeTargetOut();
        
        if (wheel == LEFT)
            hw.motorLeft.setSpeed(currentOut);
        else
            hw.motorRight.setSpeed(currentOut);
    }
    private double computeTargetOut() {
        double ff = .02 + 1.0 * targetVel/Config.MAX_VELOCITY;
        
        double actual = Config.WHEEL_RADIUS;
        if (wheel == RIGHT)
        	actual *= hw.encoderRight.getAngularSpeed();
        else
        	actual *= hw.encoderLeft.getAngularSpeed();
        
        return pid.step(actual) + ff;
    }
}
