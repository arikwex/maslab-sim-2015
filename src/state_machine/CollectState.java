package state_machine;

import core.Config;

public class CollectState extends State {
    boolean blockCollected;

    public CollectState() {
        super();
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

    }
}
