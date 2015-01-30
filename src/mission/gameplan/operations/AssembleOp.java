package mission.gameplan.operations;

import mission.TwoStack;
import mission.assmebly.Assembler;
import mission.assmebly.AssemblyStep;

public class AssembleOp extends GameOperation {
	public final TwoStack src;
	public final TwoStack dest;
	public AssembleOp(TwoStack src, TwoStack dest) {
		this.src = src;
		this.dest = dest;
	}
	public String toPlanString() {
		// Operations
		//System.out.println("ASSMEBLER GOAL: " + src + " ---> " + dest);
		AssemblyStep[] steps = Assembler.getAssemblySteps(src, dest);
		String asm = "";
		for (int i = 0; i < steps.length; i++) {
			asm += steps[i].name();
			if (i < steps.length - 1) {
				asm += "\n";
			}
		}
		return asm;
	}
}
