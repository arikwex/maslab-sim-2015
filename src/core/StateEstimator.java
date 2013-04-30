package core;

import map.Map;
import map.MapBlock;
import map.Robot;

public class StateEstimator {
    private DataCollection dc;
   
    public Map map;



    public StateEstimator(DataCollection dc) {
        this.dc = dc;    
    }
    
    public void step() {
        updatePose(); 
        updateBlocks();
    }
    
    public void updatePose() {
        double dl = dc.dLeft * Config.METERS_PER_TICK;
        double dr = dc.dRight * Config.METERS_PER_TICK;

        if (dr == 0 && dl == 0) return; // we haven't moved at all
        
        double dTheta = (dl - dr)/Config.WHEELBASE;

        Robot bot = map.bot;
        bot.pose.theta += dTheta;
        bot.pose.x += (dl+dr)*Math.cos(bot.pose.theta)/2.0;
        bot.pose.y += (dl+dr)*Math.sin(bot.pose.theta)/2.0;
    }

    public void updateBlocks() {
        MapBlock tempBlock;
        for (Block b : dc.BlocksInVision){
            tempBlock = new MapBlock(map.bot.getAbsolute(b.relX, b.relY), b.color);

            map.addBlock(tempBlock);
        }
    }
    
    public MapBlock getClosestBlock() {
        return map.closestBlock();
    }
}
