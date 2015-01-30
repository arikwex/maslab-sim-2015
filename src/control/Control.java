package control;

import hardware.Hardware;
import logging.Log;
import map.Map;
import map.geom.Point;
import map.geom.Robot;
import rrt.PathPlanning;
import state_machine.StateMachine;
import utils.Utils;
import core.Config;

public class Control {
    private static Control instance;
    
    private ControlMode mode = ControlMode.TRAVEL_PLAN;
    private Point target = null;
    
    private Hardware hw;
    private PathPlanning pp;
    private Robot bot;


    private PID rotPid;
    private PID velPid;

    public Control() {
        this.hw = Hardware.getInstance();
        this.pp = PathPlanning.getInstance();
        bot = Map.getInstance().bot;
        
        rotPid = new PID(1, 0.05, 0.7, 0.1, .25);
        rotPid.start(0, 0);

        velPid = new PID(1.5, 0, 0, 0.1, .4);
        velPid.start(0, 0);
    }
    
    public static Control getInstance() {
        if (instance == null)
            instance = new Control();
        return instance;   
    }
    
    public void setControlMode(ControlMode mode) {
    	this.mode = mode;
    }
    
    public ControlMode getMode() {
    	return this.mode;
    }
    
    public synchronized void setTarget(Point target) {
    	this.target = null;
    	StateMachine.getInstance().setGoal(null);
		StateMachine.getInstance().setPointer(null);
		if (target == null) {
			return;
		}
    	
    	if (mode == ControlMode.TRAVEL_PLAN) {
    		this.target = target;
    		StateMachine.getInstance().setGoal(target);
    	} else {
	    	this.target = new Point(target.x, target.y);
    	}
    	
    	StateMachine.getInstance().setPointer(this.target);
    }
    
    public synchronized void linkTarget(Point target) {
    	if (target == null) {
    		this.target = null;
			return;
		}
    	this.target = new Point(target.x, target.y);
    }
    
    private void setMotion(double vel, double rot) {
        setVelocity(vel - rot, vel + rot);
    }
    
    private void setVelocity(double left, double right) {   
    	hw.motorLeft.setSpeed(left);
    	hw.motorRight.setSpeed(right);
    }
    
    public void step() {
    	if (this.target == null) {
    		setMotion(0, 0);
    		return;
    	}
    	
    	if (mode == ControlMode.AIM) {
    		aim();
    	} else if (mode == ControlMode.TRAVEL_PLAN) {
    		goToWaypoint();
    	} else if (mode == ControlMode.DRIVE_FORWARD) {
     		driveForward();
     	} else if (mode == ControlMode.DRIVE_BACK) {
    		driveBackward();
    	}
    }
    
    public double getDistanceToTarget() {
    	if (target == null) {
    		return 9999;
    	}
    	return bot.pose.distance(target);
    }
    
    public double getAngleToTarget() {
    	if (target == null) {
    		return 0;
    	}
    	return Math.toDegrees(Utils.thetaDiff(bot.pose.theta, bot.pose.angleTo(target)));
    }
    
    public void aim() { 
        double thetaErr = getAngleToTarget();
        double rot = rotPid.step(-thetaErr);
        setMotion(0, rot);
    }
    
    public void driveForward() {
        double distance = getDistanceToTarget();
    	double vel = distance*4.0;
        if (vel > 0.25) {
        	vel = 0.25;
        }
        double thetaErr = getAngleToTarget();
        double rot = rotPid.step(-thetaErr);
        setMotion(vel, rot);
    }
    
    public void driveBackward() {
        double distance = getDistanceToTarget();
    	double vel = distance*4.0;
        if (vel > 0.25) {
        	vel = 0.25;
        }
        double thetaErr = 0;
        if (target != null) {
        	thetaErr = Math.toDegrees(Utils.thetaDiff(bot.pose.theta + Math.PI, bot.pose.angleTo(target)));
        }
        double rot = rotPid.step(-thetaErr);
        setMotion(-vel, rot);
    }

    public void goToWaypoint() {
        double distance = getDistanceToTarget();
        double thetaErr = getAngleToTarget();

        double vel = distance*0.4 + 0.2;
        if (vel > 0.4) {
        	vel = 0.4;
        }
        
        if (Math.abs(thetaErr) < 15) {
        	vel *= (15-Math.abs(thetaErr)) / 15;
    	} else {
            vel = 0;
        }

        double rot = rotPid.step(-thetaErr);
        setMotion(vel, rot);
    }
    
    public static void main(String[] argv) {
    	Control.getInstance().setVelocity(0.25, 0.25);
    	while (true) {}
    }
}
