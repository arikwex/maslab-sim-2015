package state_machine;

import map.Point;
import map.Pose;
import core.Config;
import core.Delta;

public class CollectState extends State {
    boolean blockCollected = false;
	private Delta delta;

    public CollectState() {
        tooLong = Config.COLLECT_TOO_LONG;
        delta = Delta.getInstance();
    }

    protected State transition() {
        if (blockCollected) {
        	delta.PutBlockInBin();
            se.numCollectedBlocks++;
            se.map.removeBlock(se.getClosestBlock());
            if (se.numCollectedBlocks >= Config.BIN_CAPACITY) {
                return new FindShelterState();
            } else {
                return new ExploreState();
            }
        }
        return this;
    }

    protected void run() {
    	Point goal = se.getClosestBlock();
    	Pose pose = se.map.bot.pose;
    	
    	sm.setGoal(goal);
    	blockCollected = (Math.abs(pose.distance(goal)) < .05);
    }
}
