package state_machine;

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

    }
}
