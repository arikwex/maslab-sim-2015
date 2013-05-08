package state_machine;

import core.Config;
import core.Delta;

public class AssemblyState extends State {

    private Delta delta;

	public AssemblyState() {
        tooLong = Config.ASSEMBLY_TOO_LONG;
        delta = Delta.getInstance();
    }

    protected State transition() {
        if (se.numCollectedBlocks == 0) {
            return new ExploreState();
        }
        return this;
    }

    protected void run() {
    	if (!se.deltaHoldingBlock) {
    		//delta.grabNextBlock();
    	}
    	else{
    		se.numCollectedBlocks--;
    	}
    }
}
