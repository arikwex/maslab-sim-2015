package state_machine;

import core.Config;
import core.Delta;

public class AssemblyState extends State {

    private Delta delta;
    boolean assembled = false;

    public AssemblyState() {
        tooLong = Config.ASSEMBLY_TOO_LONG;
        delta = Delta.getInstance();
    }

    protected State transition() {
        System.out.println("Assembly? " + delta.isDone());
        if (!delta.isDone())
            return this;
        
        if (!assembled)
            return this;
        
        delta.performSequence(Delta.TOP_OUT);
        return new ExploreState();
    }

    protected void run() {
        sm.setGoal(null);
        
        if (assembled) {
            return;
        }
        
        double[] pos, posShifted;
        for (int i = 0; i < Config.ASSEMBLY_PYRAMID.length; i++) {
            pos = Config.ASSEMBLY_PYRAMID[i];
            posShifted = pos.clone();
            posShifted[2] += 10;

            delta.performSequence(delta.PICK_LEFT);
            delta.performSequence(new double[][]{posShifted, pos, {1,1}, {1,0}, posShifted});
            se.numCollectedBlocks--;
        }
        assembled = true;
    }
}
