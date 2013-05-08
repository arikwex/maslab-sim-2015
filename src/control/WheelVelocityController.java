package control;

import uORCInterface.OrcController;
import core.Config;
import data_collection.DataCollection;
import data_collection.EncoderPair;

public class WheelVelocityController {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    
    private int wheel;
    private EncoderPair enc;
    
    private double targetVel = 0;
    private int currPwm = 0;
    private long prevTime;
    
    private static final int SLEW_LIM = 1000; //pwm units per second
    private PID pid;
    
    private OrcController orc;
    
    
    public WheelVelocityController(OrcController orc, int wheel) {
        this.orc = orc;
        this.wheel = wheel;
        this.pid = new PID(0, 5, 0, 0, 128);
        pid.start(0, 0);
        prevTime = System.currentTimeMillis();
        enc = DataCollection.getInstance().getEncoders();
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
        
        orc.motorSet(wheel, currPwm);
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
        double ff = .75 * 255 * targetVel/Config.MAX_VELOCITY;
        
        double actual = Config.METERS_PER_TICK;
        if (wheel == LEFT)
            actual *= enc.dLeft / enc.dt;
        else
            actual *= enc.dRight / enc.dt;
        
        return (int)Math.round(pid.step(actual) + ff);
    }
    
    public static void main(String[] args) {
		OrcController orc = new OrcController(new int[]{0,1});
        WheelVelocityController left = new WheelVelocityController(orc, 0);
        WheelVelocityController right = new WheelVelocityController(orc, 1);
        left.setVelocity(-.2);
        right.setVelocity(-.2);
		while (true) {
			left.step();
			right.step();
			try { Thread.sleep(50);} catch (Exception e){};
		}
	}
}
