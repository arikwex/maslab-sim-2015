package core;

import hardware.Hardware;
import map.Map;
import map.Pose;
import map.geom.Robot;
import map.geom.Segment;
import utils.Utils;

public class StateEstimator implements Runnable {
    private static StateEstimator instance;
    private Hardware hw;
    public Map map;
    boolean started;

    private StateEstimator() {
        map = Map.getInstance();
        hw = Hardware.getInstance();
        started = false;
    }

    public static StateEstimator getInstance() {
        if (instance == null)
            instance = new StateEstimator();
        return instance;
    }

    public void step() {
        updatePoseEncoder();
    }

    private void updatePoseEncoder() {
        double dl = hw.encoderLeft.getDeltaAngularDistance() * Config.WHEEL_RADIUS;
        double dr = hw.encoderRight.getDeltaAngularDistance() * Config.WHEEL_RADIUS;

        if (dr == 0 && dl == 0)
            return; // we haven't moved at all

        double dTheta = (dr - dl) / Config.WHEELBASE;
        Robot bot = Map.getInstance().bot;

        double newX = bot.pose.x + (dl + dr) * Math.cos(bot.pose.theta) / 2.0;
        double newY = bot.pose.y + (dl + dr) * Math.sin(bot.pose.theta) / 2.0;
        double newTheta = bot.pose.theta + dTheta;
        
        newTheta = Utils.wrapAngle(newTheta);
        
        Pose nextPose = new Pose(newX, newY, newTheta);
        bot.pose = nextPose;

        if (map.checkSegment(new Segment(bot.pose,nextPose),bot.pose.theta))
            bot.pose = nextPose;

        if (bot.gripping != null) {
        	bot.gripping.pt = bot.getGripPoint();
        }
    }
    
    public String toString() {
        return map.bot.pose.toString();
    }

    @Override
    public void run() {
        while (true) {
            step();
        }
    }
}
