package core;

import data_collection.DataCollection;
import logging.Log;
import map.Map;
import map.MapBlock;
import map.Robot;

public class StateEstimator {
    private static StateEstimator instance;
    
    private Map map;

    private StateEstimator() {
        map = Map.getInstance();
    }    
    
    public static StateEstimator getInstance() {
        if (instance == null)
            instance = new StateEstimator();
        return instance;   
    }
    
    public void step() {
        updatePose(); 
        Log.log(this.toString());
        //updateBlocks();
    }
    
    public void updatePose() {
        DataCollection dc = DataCollection.getInstance();
        
        double dl = dc.encoders.dLeft * Config.METERS_PER_TICK;
        double dr = dc.encoders.dRight * Config.METERS_PER_TICK;

        if (dr == 0 && dl == 0) return; // we haven't moved at all
        
        double dTheta = Math.toDegrees((dr - dl)/Config.WHEELBASE);
        
        Robot bot = Map.getInstance().bot;
        bot.pose.theta += dTheta;
        bot.pose.x += (dl+dr)*Math.cos(Math.toRadians(bot.pose.theta))/2.0;
        bot.pose.y += (dl+dr)*Math.sin(Math.toRadians(bot.pose.theta))/2.0;
    }

    public void updateBlocks() {
        DataCollection dc = DataCollection.getInstance();
        
        MapBlock tempBlock;
        for (Block b : dc.BlocksInVision){
            tempBlock = new MapBlock(Map.getInstance().bot.getAbsolute(b.relX, b.relY), b.color);

            map.addBlock(tempBlock);
        }
    }
    
    public MapBlock getClosestBlock() {
        return map.closestBlock();
    }
    
    public String toString() {
        return map.bot.pose.toString(); 
    }
}
