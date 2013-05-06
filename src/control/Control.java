package control;

import rrt.PathPlanning;
import map.Map;
import map.Point;
import map.Robot;
import map.Segment;
import uORCInterface.OrcController;

public class Control {
    private static Control instance;
    
    private PathPlanning pp;
    private Robot bot;

    private WheelVelocityController leftController;
    private WheelVelocityController rightController;

    private PID rotPid;
    private PID velPid;

    public Control() {
    	OrcController orc = new OrcController(new int[]{0,1});
        this.pp = PathPlanning.getInstance();
        bot = Map.getInstance().bot;
        
        rotPid = new PID(.05, 0, 0, 0, .22);
        rotPid.start(0, 0);

        velPid = new PID(3, 0, 0, 0, .3);
        velPid.start(0, 0);
        
        leftController = new WheelVelocityController(orc, WheelVelocityController.LEFT);
        rightController = new WheelVelocityController(orc, WheelVelocityController.RIGHT);

    }
    
    public static Control getInstance() {
        if (instance == null)
            instance = new Control();
        return instance;   
    }
    
    private void setMotion(double vel, double rot) {
        setVelocity(vel + rot, vel - rot);
    }
    
    private void setVelocity(double left, double right) {    	
    	leftController.setVelocity(left);
        rightController.setVelocity(right);
    }
    
    public void step() {
        goToWaypoint();
        leftController.step();
        rightController.step();
    }

    public void goToWaypoint() {
        Point wayPoint = pp.getNextWaypoint();
        Segment seg = new Segment(bot.pose,wayPoint);
        if (!Map.getInstance().checkSegment(seg, bot.pose.theta)){
        	System.out.println("GetNextWaypoint Gave a bad Waypoint !!! seg: "+seg);
        	/*
        	while (true){
	        	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	*/
        }
        
        System.out.println("From: " + bot.pose + " to:" + wayPoint);
        
        double distance = bot.pose.distance(wayPoint);
        double angle = Math.toDegrees(bot.pose.angleTo(wayPoint));
        double thetaErr = angle - Math.toDegrees(bot.pose.theta);
        
        if (thetaErr > 180)
            thetaErr -= 360;
        else if (thetaErr < -180)
            thetaErr += 360;

        double vel = velPid.step(distance);
        if (Math.abs(thetaErr) < 7)
        	vel *= (7-Math.abs(thetaErr)) / 7;
        else
            vel = 0;

        double rot = rotPid.step(-thetaErr);
        
        setMotion(vel, rot);
    }
}
