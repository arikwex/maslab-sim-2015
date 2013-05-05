package control;

import rrt.PathPlanning;
import map.Map;
import map.Point;
import map.Robot;
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
        
        rotPid = new PID(.035, 0, 0, 0, .3);
        rotPid.start(0, 0);

        velPid = new PID(3, 0, 0, 0, .4);
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
    	System.out.println("Setting velocity to left "+left + " right "+ right);
    	
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
        
        System.out.println("From: " + bot.pose + " to:" + wayPoint);
        
        double distance = bot.pose.distance(wayPoint);
        double angle = bot.pose.angleTo(wayPoint);
        
        System.out.println("a: " + angle);
        
        System.out.println("Distance: " + distance);
        double thetaErr = angle - bot.pose.theta;
        System.out.println("Theta Error: " + thetaErr);
        
        if (thetaErr > 180)
            thetaErr -= 360;
        else if (thetaErr < -180)
            thetaErr += 360;

        double vel = velPid.step(distance);
        if (Math.abs(thetaErr) < 7)
            vel *= Math.pow(7 - Math.abs(thetaErr), 2) / 49;
        else
            vel = 0;

        double rot = rotPid.step(-thetaErr);
        
        setMotion(vel, rot);
    }
}
