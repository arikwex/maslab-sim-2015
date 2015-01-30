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
        
        //rotPid = new PID(1, 0.05, 0.7, 0.1, .25);
        rotPid = new PID(0.7, 0.3, 0.0, 0.1, 0.2);
        rotPid.start(0, 0);

        //velPid = new PID(1.5, 0, 0, 0.1, .4);
        velPid = new PID(1.0, 0.3, 0, 0.05, 0.25);
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

    public void step() {
    	if (this.target == null) {
    		setMotion(0, 0);
    		return;
    	}
    	
    	goToWaypoint();
    }
    
    public double getDistanceToTarget() {
    	if (target == null) {
    		return 0;
    	}
    	return bot.pose.distance(target);
    }
    
    public double getAdjustedDistanceToTarget() {
    	if (this.getMode() == ControlMode.AIM)
        	return 0;
    
    	double distance = getDistanceToTarget() + 0.10;
        
        if (this.getMode() == ControlMode.DRIVE_BACK)
        	distance *= -1;

        return distance;
    }
    
    public double getAngleToTarget() {
    	if (target == null) {
    		return 0;
    	}
    	
    	if (this.getMode() == ControlMode.DRIVE_BACK)
    		return Utils.thetaDiff(bot.pose.theta - Math.PI, bot.pose.angleTo(target));
    	return Utils.thetaDiff(bot.pose.theta, bot.pose.angleTo(target));
    }
    
    public void goToWaypoint() {
        double thetaErr = getAngleToTarget();
        double distance = getAdjustedDistanceToTarget();
                
        if (Math.abs(thetaErr) < Math.PI/8) {
        	distance *= Math.cos(thetaErr * 2);
    	} else {
            distance = 0;
        }

        double rot = rotPid.step(-thetaErr);
        double vel = velPid.step(-distance);
        setMotion(vel, rot);
    }
    
    
    private void setMotion(double vel, double rot) {
        setVelocity(vel - rot, vel + rot);
    }
    
    private void setVelocity(double left, double right) {
    	hw.motorLeft.setSpeed(left * Config.MAX_VELOCITY);
    	hw.motorRight.setSpeed(right * Config.MAX_VELOCITY);
    }
    
    private double round(double val, double digits) {
    	return Math.round(val*Math.pow(10, digits))/Math.pow(10,digits);
    }
}
