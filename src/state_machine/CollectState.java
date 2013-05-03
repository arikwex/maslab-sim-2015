package state_machine;

import map.Segment;
import core.Config;

public class CollectState extends State {
    boolean blockCollected;

    public CollectState(StateMachine sm) {
        super(sm);
        tooLong = Config.COLLECT_TOO_LONG;
    }

    protected State transition() {
        if (blockCollected) {
            se.numCollectedBlocks++;
            if (se.numCollectedBlocks >= Config.BIN_CAPACITY) {
                return new FindShelterState(sm);
            } else {
                return new ExploreState(sm);
            }
        }
        return this;
    }

    protected void run() {
    	blockCollected = (se.map.bot.pose.distance(sm.goal) == 0 || (se.map.bot.pose.angleTo(sm.goal) - se.map.bot.pose.theta == 180));
    	if (!blockCollected && Math.abs(se.map.bot.pose.angleTo(sm.goal) - se.map.bot.pose.theta) > 30){
    		Segment toGoal = new Segment(se.map.bot.pose,sm.goal);
    		toGoal = toGoal.scale(1.5).trimToLegal(se.map);
    		sm.setGoal(se.getClosestBlock());
    	}
    }
}
