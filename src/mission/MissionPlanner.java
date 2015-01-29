package mission;

public class MissionPlanner {
	public static MissionPlanner mp = null;
	
	public MissionPlanner() {
	}
	
	public static MissionPlanner getInstance() {
		if (mp == null) {
			mp = new MissionPlanner();
		}
		return mp;
	}
	
	public TwoStack mergeTwoStack(TwoStack twoStack) {
		return null;
	}
	
	public static void main(String[] args) {
		TwoStack src = new TwoStack("RGR", "GRG");
		TwoStack dest = new TwoStack("RRR", "GGG");
		AssemblyStep[] steps = Assembler.getAssemblySteps(src, dest);
		System.out.println("# of steps = " + steps.length);
		for (int i = 0; i < steps.length; i++) {
			System.out.println(steps[i].name());
		}
		
		/*
		AssemblyState asm = new AssemblyState("RRR", "GGG", "");
		asm = asm.apply(AssemblyStep._T1);
		asm = asm.apply(AssemblyStep._MID);
		asm = asm.apply(AssemblyStep._GRAB);
		asm = asm.apply(AssemblyStep._T3);
		asm = asm.apply(AssemblyStep._BOT);
		asm = asm.apply(AssemblyStep._DEPLOY);
		System.out.println("Final = " + asm.toString());
		*/
	}
}
