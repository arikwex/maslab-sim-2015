package state_machine;

import map.Point;
import map.Segment;
import core.Config;

public class CollectState extends State {
    boolean blockCollected;

    public CollectState() {
        tooLong = Config.COLLECT_TOO_LONG;
    }

    protected State transition() {
        if (blockCollected) {
            se.numCollectedBlocks++;
            if (se.numCollectedBlocks >= Config.BIN_CAPACITY) {
                return new FindShelterState();
            } else {
                return new ExploreState();
            }
        }
        return this;
    }

    protected void run() {
    	Point goal = sm.getGoal();
    	blockCollected = (se.map.bot.pose.distance(goal) == 0 || (se.map.bot.pose.angleTo(goal) - se.map.bot.pose.theta == 180));
    	if (!blockCollected && Math.abs(se.map.bot.pose.angleTo(goal) - se.map.bot.pose.theta) > 30){
    		Segment toGoal = new Segment(se.map.bot.pose,goal);
    		toGoal = toGoal.scale(1.5).trimToLegal(se.map);
    		sm.setGoal(se.getClosestBlock());
    	}
    }
}
