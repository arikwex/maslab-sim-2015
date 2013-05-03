package control;

import MotorControlSolution.RobotBase;
import MotorControlSolution.RobotVelocityController;
import MotorControlSolution.RobotVelocityControllerBalanced;
import orc.Orc;
import rrt.PathPlanning;
import map.Map;
import map.Point;
import map.Robot;
import uORCInterface.OrcController;

public class Control {
    private static Control instance;
    
    private PathPlanning pp;
    private Robot bot;

    private MotorControlSolution.WheelVelocityController leftController;
    private MotorControlSolution.WheelVelocityController rightController;
    
    private PID rotPid;
    private PID velPid;

    public Control() {
    	this(Map.getInstance(),PathPlanning.getInstance(),new OrcController(new int[]{0,1}));
    }   
    
    public Control(Map m, PathPlanning pp, OrcController orc) {
    	RobotBase robot = new RobotBase();
	    RobotVelocityController robotVelocityController = null;
	    robotVelocityController = new RobotVelocityControllerBalanced();
	    robot.setRobotVelocityController(robotVelocityController);
	    robotVelocityController.setGain(1);
	    for(int i = 0; i < 2; i++){
		robotVelocityController.getWheelVelocityController(i).setGain(6);
	    }
	    robot.enableMotors(true);
	    
        this.pp = pp;
        bot = m.bot;
        
        rotPid = new PID(.0035, 0, 0, 0, .3);
        rotPid.start(0, 0);

        velPid = new PID(3, 0, 0, 0, .8);
        velPid.start(0, 0);
        /*
        leftController = new WheelVelocityController(orc, WheelVelocityController.LEFT);
        rightController = new WheelVelocityController(orc, WheelVelocityController.RIGHT);
        */
        leftController = robotVelocityController.getWheelVelocityController(WheelVelocityController.LEFT);
        rightController = robotVelocityController.getWheelVelocityController(WheelVelocityController.RIGHT);
        
        //--------------------------------------
        
    }
    
    public static Control getInstance() {
        if (instance == null)
            instance = new Control();
        return instance;   
    }
    
    public static Control getInstance(Map m, PathPlanning pp, OrcController orc) {
        if (instance == null)
            instance = new Control(m, pp, orc);
        return instance;   
    }

    private void setMotion(double vel, double rot) {
        setVelocity(vel + rot, vel - rot);
    }
    
    private void setVelocity(double left, double right) {
    	leftController.setDesiredAngularVelocity(left);
    	rightController.setDesiredAngularVelocity(right);
    	/*
    	leftController.setVelocity(left);
        rightController.setVelocity(right);
    	 */
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
