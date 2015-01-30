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
        double targetOut = computeTargetOut();
                
        //applySlew(targetPwm);
        currentOut = targetOut;
        
        // TODO: Double check that currPwm is okay in setSpeed
        if (wheel == LEFT)
            hw.motorLeft.setSpeed(currentOut);
        else
            hw.motorRight.setSpeed(currentOut);
    }
    
    private void applySlew(int targetPwm) {
        long now = System.currentTimeMillis();
        int cap = (int)((now-prevTime)/1000.0 * SLEW_LIM);
        double dPwm = targetPwm - currentOut;
        if (cap > 0) {
        	prevTime = now;
        	if (dPwm < 0) {
        		currentOut -= cap;
        		if (currentOut < targetPwm)
        			currentOut = targetPwm;
        	} else {
        		currentOut += cap;
        		if (currentOut > targetPwm)
        			currentOut = targetPwm;
        	}
        	
        }
    }

    private double computeTargetOut() {
        double ff = .02 + 1.0 * targetVel/Config.MAX_VELOCITY;
        
        double actual = Config.WHEEL_RADIUS;
        if (wheel == RIGHT)
        	actual *= hw.encoderRight.getAngularSpeed();
        else
        	actual *= hw.encoderLeft.getAngularSpeed();
        
        System.out.println("Actual: " + wheel + " " + actual + " desired " + targetVel);
        
        return pid.step(actual) + ff;
    }
}
