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
    	this(Map.getInstance(),PathPlanning.getInstance());
    }   
    
    public Control(Map m, PathPlanning pp) {

    	
    	OrcController orc = new OrcController(new int[]{0,1});
        this.pp = pp;
        bot = m.bot;
        
        rotPid = new PID(.0035, 0, 0, 0, .3);
        rotPid.start(0, 0);

        velPid = new PID(3, 0, 0, 0, .8);
        velPid.start(0, 0);
        
        leftController = new WheelVelocityController(orc, WheelVelocityController.LEFT);
        rightController = new WheelVelocityController(orc, WheelVelocityController.RIGHT);
    }
    
    public static Control getInstance() {
        if (instance == null)
            instance = new Control();
        return instance;   
    }
    
    public static Control getInstance(Map m, PathPlanning pp) {
        if (instance == null)
            instance = new Control(m, pp);
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
    }

    public void goToWaypoint() {
        Point wayPoint = pp.getNextWaypoint();
        
        double distance = bot.pose.distance(wayPoint);
        double angle = bot.pose.angleTo(wayPoint);
        
        double thetaErr = bot.pose.theta - angle;
        
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
