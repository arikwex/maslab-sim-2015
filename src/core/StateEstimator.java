package core;

import com.googlecode.javacv.ImageTransformer.Data;

import data_collection.DataCollection;
import data_collection.EncoderPair;
import logging.Log;
import map.Map;
import map.MapBlock;
import map.Pose;
import map.Robot;
import map.Segment;

public class StateEstimator {
    private static StateEstimator instance;

    private DataCollection dc;

    boolean ready = false;
    public boolean[] tooClose;
    public boolean anyTooClose;

    public int numCollectedBlocks;

    public int numBlocksLeft;

    public Map map;

    private StateEstimator() {
        map = Map.getInstance();
        dc = DataCollection.getInstance();
    }
    public static StateEstimator getInstance() {
        if (instance == null)
            instance = new StateEstimator();
        return instance;
    }

    
    public StateEstimator(DataCollection dc) {
        this.dc = dc;
        //tooClose = new boolean[dc.getSonars().size()];
        numCollectedBlocks = 0;
    }

    public void step() {
        updatePose();
        updateBlocks();
        //sonarCheck();

        Log.log(this.toString());
    }

    public void updatePose() {
    	if (dc.getEncoders() == null){
    		return;
    	}
        EncoderPair enc = dc.getEncoders();

        double dl = enc.dLeft * Config.METERS_PER_TICK;
        double dr = enc.dRight * Config.METERS_PER_TICK;

        if (dr == 0 && dl == 0)
            return; // we haven't moved at all

        double dTheta = Math.toDegrees((dr - dl) / Config.WHEELBASE);
        Robot bot = Map.getInstance().bot;
        
        Pose nextPose = new Pose(bot.pose.x+(dl + dr) * Math.cos(Math.toRadians(bot.pose.theta)) / 2.0,
        		bot.pose.y+ (dl + dr) * Math.sin(Math.toRadians(bot.pose.theta)) / 2.0,
        		bot.pose.theta + dTheta);

        if (map.checkSegment(new Segment(bot.pose,nextPose)))
        	bot.pose = nextPose;
    }

    public void updateBlocks() {

        MapBlock tempBlock;
        for (Block b : dc.getBlocks()) {
            tempBlock = new MapBlock(Map.getInstance().bot.getAbsolute(b.relX, b.relY), b.color);

            map.addBlock(tempBlock);
        }
    }
/*
    public void sonarCheck() {
        anyTooClose = false;
        if (tooClose == null)
        	return;
        for (int i = 0; i < tooClose.length; i++) {
            tooClose[i] = (dc.getSonars().get(i).meas < Config.TOOCLOSE);
            if (tooClose[i])
                anyTooClose = true;
        }
    }
*/
    public MapBlock getClosestBlock() {
        return map.closestBlock();
    }

    public String toString() {
        return map.bot.pose.toString();
    }
}
