package state_machine;

import core.Config;

public class AssemblyState extends State {

    public AssemblyState() {
        tooLong = Config.ASSEMBLY_TOO_LONG;
    }

    protected State transition() {
        if (se.numCollectedBlocks == 0) {
            return new ExploreState();
        }
        return this;
    }

    protected void run() {

    }
}
