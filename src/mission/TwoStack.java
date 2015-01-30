package mission;

import java.util.HashMap;

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
			TwoStack ts = reorder(i);
			stacks.put(ts.A + "," + ts.B, ts);
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
	public TwoStack reorder(int i) {
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
			stacks.put(ts.A + "," + ts.B, ts);
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
}
