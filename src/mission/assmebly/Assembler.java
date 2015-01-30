package mission.assmebly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import mission.TwoStack;

public class Assembler {
	public static AssemblyStep[] getAssemblySteps(TwoStack src, TwoStack dest) {
		Queue<AssemblyState> open = new PriorityQueue<AssemblyState>(50000, new Comparator<AssemblyState>() {
			@Override
			public int compare(AssemblyState o1, AssemblyState o2) {
				return o1.getCost() - o2.getCost();
			}
		});
		HashMap<String, Integer> visited = new HashMap<String, Integer>();

		AssemblyState start = new AssemblyState(src.A, src.B, "");
		open.add(start);
		visited.put(start.toString(), 0);

		while (open.size() > 0) {
			AssemblyState current = open.poll();
			if (current.toTwoStack().toString().equals(dest.toString())) {
				//System.out.println("SOLVED! Cost = " + current.getCost());
				return backtrace(current);
			}
			AssemblyStep[] allowedSteps = current.getAllowedSteps();
			for (AssemblyStep asm : allowedSteps) {
				AssemblyState newState = current.apply(asm);
				if (!visited.containsKey(newState.toString())) {
					open.add(newState);
					visited.put(newState.toString(), 0);
				}
			}
		}
		
		return null;
	}
	
	private static AssemblyStep[] backtrace(AssemblyState current) {
		List<AssemblyStep> steps = new ArrayList<AssemblyStep>();
		while (current.getParent() != null) {
			steps.add(0, current.getStep());
			current = current.getParent();
		}
		return steps.toArray(new AssemblyStep[steps.size()]);
	}
}
