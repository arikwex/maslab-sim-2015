package mission.gameplan.operations;

import mission.TwoStack;

public class AssembleOp extends GameOperation {
	public final TwoStack twoStack;
	public AssembleOp(TwoStack twoStack) {
		this.twoStack = twoStack;
	}
}
