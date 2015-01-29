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
		return "[" + A + "], [" + B + "]";
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
