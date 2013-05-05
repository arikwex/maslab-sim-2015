package state_machine;

import map.MapBlock;
import map.Point;
import map.Pose;
import map.Segment;
import core.Config;

public class CollectState extends State {
    boolean blockCollected = false;

    public CollectState() {
        tooLong = Config.COLLECT_TOO_LONG;
    }

    protected State transition() {
        if (blockCollected) {
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
