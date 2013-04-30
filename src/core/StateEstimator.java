package core;

import map.Map;
import map.MapBlock;
import map.Robot;

public class StateEstimator extends Thread {


	private DataCollection dc;
   
    public Map map;

	private boolean ready = false;
	public boolean[] tooClose;
	public boolean anyTooClose;

	public int numCollectedBlocks;

	public int numBlocksLeft;



    public StateEstimator(DataCollection dc) {
        this.dc = dc;    
        tooClose = new boolean[dc.numSonars];
        numCollectedBlocks = 0;
    }
    
    public void step() {
        updatePose(); 
        updateBlocks();
        sonarCheck();
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
    
    public void sonarCheck() {
    	anyTooClose = false;
		for (int i=0; i< dc.sonars.size();i++){
			tooClose[i] = (dc.sonars.get(i).meas < Config.TOOCLOSE);
			if (tooClose[i])
				anyTooClose = true;
		}
	}
    
    public MapBlock getClosestBlock() {
        return map.closestBlock();
    }
    
    public void run(){
    	dc.start();
    	while (true){
    		while (!dc.ready){
        		try {Thread.sleep((long) 0.0001);} catch (InterruptedException e) {}
        	}
    		step();
        	ready  = true;
    	}
    }
}
