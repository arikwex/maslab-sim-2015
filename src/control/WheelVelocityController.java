package control;

import hardware.Hardware;
import core.Config;

public class WheelVelocityController {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    private int wheel;
    
    private double targetVel = 0;
    private int currPwm = 0;
    private long prevTime;
    
    private static final int SLEW_LIM = 1000; //pwm units per second
    private PID pid;
    
    private Hardware hw;
    
    
    public WheelVelocityController(Hardware hw, int wheel) {
        this.hw = hw;
        this.wheel = wheel;
        this.pid = new PID(0, 5, 0, 0, 128);
        pid.start(0, 0);
        prevTime = System.currentTimeMillis();
    }
    
    public void setVelocity(double v) {
        targetVel = v;
        pid.setTarget(targetVel);
    }
    
    public void step() {
        int targetPwm = computeTargetPWM();
        if (wheel == LEFT)
        	targetPwm*=-1;
        
        applySlew(targetPwm);
        //currPwm = targetPwm;
        
        // TODO: Double check that currPwm is okay in setSpeed
        if (wheel == LEFT)
            hw.motorLeft.setSpeed(currPwm);
        else
            hw.motorRight.setSpeed(currPwm);
    }
    
    private void applySlew(int targetPwm) {
        long now = System.currentTimeMillis();
        int cap = (int)((now-prevTime)/1000.0 * SLEW_LIM);
        int dPwm = targetPwm - currPwm;
        if (cap > 0) {
        	prevTime = now;
        	if (dPwm < 0) {
        		currPwm -= cap;
        		if (currPwm < targetPwm)
        			currPwm = targetPwm;
        	} else {
        		currPwm += cap;
        		if (currPwm > targetPwm)
        			currPwm = targetPwm;
        	}
        	
        }
    }

    private int computeTargetPWM() {
        double ff = 1.0 * 255 * targetVel/Config.MAX_VELOCITY;
        
        double actual = Config.WHEEL_CIRCUMFERENCE;
        if (wheel == LEFT)
            actual *= hw.encoderLeft.getAngularSpeed();
        else
            actual *= hw.encoderRight.getAngularSpeed();
        
        return (int)Math.round(pid.step(actual) + ff);
    }
    
    public static void main(String[] args) {
		Hardware hw = Hardware.getInstance();
        WheelVelocityController left = new WheelVelocityController(hw, 0);
        WheelVelocityController right = new WheelVelocityController(hw, 1);
        left.setVelocity(-.2);
        right.setVelocity(-.2);
		while (true) {
			left.step();
			right.step();
			try { Thread.sleep(50);} catch (Exception e){};
		}
	}
}
