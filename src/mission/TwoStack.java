package mission;

import java.util.HashMap;

import core.Config;

public class TwoStack {
	// Stack strings
	public final String A, B;
	
	public TwoStack(String A, String B) {
		this.A = A;
		this.B = B;
	}
	
	public String toString() {
		return "[" + A + "|" + B + "]";
	}
	
	public TwoStack[] getReorderOptions() {
		HashMap<String, TwoStack> stacks = new HashMap<String, TwoStack>();
		for (int i = 0; i <= 5; i++ ){
			TwoStack ts = reorder(i, false);
			TwoStack ts2 = reorder(i, true);
			if (worthy(ts)) {
				stacks.put(ts.A + "," + ts.B, ts);
			}
			if (worthy(ts2)) {
				stacks.put(ts2.A + "," + ts2.B, ts2);
			}
		}
		TwoStack[] reorderOps = new TwoStack[stacks.keySet().size()];
		int i = 0;
		for (String str : stacks.keySet()) {
			reorderOps[i] = stacks.get(str);
			i++;
		}
		return reorderOps;
	}
	
	/* Assumes that one stack is empty */
	public TwoStack reorder(int i, boolean inverse) {
		String stack = A;
		boolean isA = true;
		if (stack.length() == 0) {
			stack = B;
			isA = false;
		}
		if (i == 0) {
			stack = "" + stack.charAt(0) + stack.charAt(1) + stack.charAt(2);
		} else if (i == 1) {
			stack = "" + stack.charAt(0) + stack.charAt(2) + stack.charAt(1);
		} else if (i == 2) {
			stack = "" + stack.charAt(1) + stack.charAt(0) + stack.charAt(2);
		} else if (i == 3) {
			stack = "" + stack.charAt(1) + stack.charAt(2) + stack.charAt(0);
		} else if (i == 4) {
			stack = "" + stack.charAt(2) + stack.charAt(0) + stack.charAt(1);
		} else if (i == 5) {
			stack = "" + stack.charAt(2) + stack.charAt(1) + stack.charAt(0);
		}
		
		isA ^= inverse;
		if (isA) {
			return new TwoStack(stack, "");
		} else {
			return new TwoStack("", stack);
		}
	}
	
	public TwoStack[] getCrossOptions() {
		HashMap<String, TwoStack> stacks = new HashMap<String, TwoStack>();
		for (int i = 0; i <= 7; i++ ){
			TwoStack ts = cross(i);
			if (worthy(ts)) {
				stacks.put(ts.A + "," + ts.B, ts);
			}
		}
		TwoStack[] crossOps = new TwoStack[stacks.keySet().size()];
		int i = 0;
		for (String str : stacks.keySet()) {
			crossOps[i] = stacks.get(str);
			i++;
		}
		return crossOps;
	}
	
	public TwoStack cross(int i) {
		String a = "";
		String b = "";
		if ((i&0x1) == 0x1) {
			a += B.charAt(0);
			b += A.charAt(0);
		} else {
			a += A.charAt(0);
			b += B.charAt(0);
		}
		
		if ((i&0x2) == 0x2) {
			a += B.charAt(1);
			b += A.charAt(1);
		} else {
			a += A.charAt(1);
			b += B.charAt(1);
		}
		
		if ((i&0x4) == 0x4) {
			a += B.charAt(2);
			b += A.charAt(2);
		} else {
			a += A.charAt(2);
			b += B.charAt(2);
		}
		return new TwoStack(a, b);
	}
	
	public boolean worthy(TwoStack ts) {
		if (worthyStack(ts.A) && worthyStack(ts.B)) {
			return true;
		}
		return false;
	}
	
	public boolean worthyStack(String stack) {
		if (stack.length() == 0) {
			return true;
		}
		if (stack.charAt(stack.length()-1) == Config.TEAM_COLOR) {
			return true;
		}
		if (stack.equals("RRR") || stack.equals("GGG")) {
			return true;
		}
		return false;
	}
}
