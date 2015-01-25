package control;

import hardware.Hardware;
import logging.Log;
import map.Map;
import map.geom.Point;
import map.geom.Robot;
import rrt.PathPlanning;
import utils.Utils;
import core.Config;

public class Control {
    private static Control instance;
    
    private Hardware hw;
    private PathPlanning pp;
    private Robot bot;

    private WheelVelocityController leftController;
    private WheelVelocityController rightController;

    private PID rotPid;
    private PID velPid;

    public Control() {
        this.hw = Hardware.getInstance();
        this.pp = PathPlanning.getInstance();
        bot = Map.getInstance().bot;
        
        rotPid = new PID(1, 0.0, 0.0, 0.1, .15);
        rotPid.start(0, 0);

        velPid = new PID(1.5, 0, 0, 0.1, .3);
        velPid.start(0, 0);
        
        leftController = new WheelVelocityController(hw, WheelVelocityController.LEFT);
        rightController = new WheelVelocityController(hw, WheelVelocityController.RIGHT);
    }
    
    public static Control getInstance() {
        if (instance == null)
            instance = new Control();
        return instance;   
    }
    
    private void setMotion(double vel, double rot) {
        setVelocity(vel - rot, vel + rot);
    }
    
    private void setVelocity(double left, double right) {    	
    	leftController.setVelocity(left);
        rightController.setVelocity(right);
        //Log.log("Set left controller to "+left+" Set right controller to "+right);
    }
    
    public void step() {
        goToWaypoint();
        leftController.step();
        rightController.step();
    }

    public void goToWaypoint() {
        Point wayPoint = pp.getNextWaypoint();
        
        if (wayPoint == null) {
        	setMotion(0,0);
        	return;
        }
                
        double distance = bot.pose.distance(wayPoint);
        double thetaErr = Math.toDegrees(Utils.thetaDiff(bot.pose.theta, bot.pose.angleTo(wayPoint)));

        double vel = velPid.step(-distance);
        Log.log("Desired vel: " + vel);
        
        if (Math.abs(thetaErr) < 7)
        	vel *= (7-Math.abs(thetaErr)) / 7;
        else
            vel = 0;

        double rot = rotPid.step(-thetaErr);
        Log.log("Desired rot: " + rot);
        
        setMotion(vel, rot);
        Log.log("From: " + bot.pose + " to:" + wayPoint + " with theta " + bot.pose.angleTo(wayPoint) + " and distance " + distance + " theta err " + thetaErr);
    }
}
