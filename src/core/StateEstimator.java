package core;

import java.util.ArrayList;

import map.Map;
import map.MapBlock;
import map.Robot;

public class StateEstimator {
    private DataCollection dc;
   
    Map map;

    public static final double WHEELBASE = .4;
    public static final double TICKS_PER_REV = 65500;
    public static final double WHEEL_RADIUS = .0625;
    //0.0000059954
    public static final double METERS_PER_TICK = WHEEL_RADIUS*Math.PI*2/TICKS_PER_REV;

    public StateEstimator(DataCollection dc) {
        this.dc = dc;    
    }
    
    public void step() {
        updatePose(); 
        updateBlocks();
    }
    
    public void updatePose() {
        double dl = dc.encoders.dLeft * METERS_PER_TICK;
        double dr = dc.encoders.dRight * METERS_PER_TICK;

        if (dr == 0 && dl == 0) return; // we haven't moved at all
        
        double dTheta = (dl - dr)/WHEELBASE;

        Robot bot = map.bot;
        bot.theta += dTheta;
        bot.center.x += (dl+dr)*Math.cos(bot.theta)/2.0;
        bot.center.y += (dl+dr)*Math.sin(bot.theta)/2.0;
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
