package state_machine;

import map.Map;
import map.Point;
import map.Pose;
import core.Config;
import core.Delta;

public class CollectState extends State {
    boolean blockCollected = false;
    boolean disposed = false;
	private Delta delta;

    public CollectState() {
        tooLong = Config.COLLECT_TOO_LONG;
        delta = Delta.getInstance();
    }

    protected State transition() {
        System.out.println("Done? " + delta.isDone());
        if (!delta.isDone())
            return this;
        
        if (disposed) {
            return new ExploreState();
        }
        if (blockCollected) {
            se.numCollectedBlocks++;
            return new ExploreState();
            /*
            if (se.numCollectedBlocks >= Config.BIN_CAPACITY) {
                return new FindShelterState();
            } else {
                return new ExploreState();
            }*/
        }
        return this;
    }

    protected void run() {
    	sm.setGoal(Map.getInstance().bot.pose);
    	if (disposed || blockCollected)
    	    return;
    	
    	if (se.getCaptureStatus() == 2) {
    	    delta.performSequence(Delta.DISPOSE_DOUBLE);
    	    disposed = true;
    	    
    	} else if (se.getCaptureStatus() == 1) {
    	    delta.performSequence(Delta.PICK_SINGLE);
    	    delta.performSequence(Delta.DELIVER_LEFT);
    	    blockCollected = true;
    	}
    }
}
