package mission;

public class TwoStack {
	// Stack strings
	public final String A, B;
	
	// Assembly time
	public final AssemblyStep[] steps;
	
	public TwoStack(String A, String B) {
		this.A = A;
		this.B = B;
		this.steps = null;
	}
	
	public TwoStack(String A, String B, AssemblyStep[] steps) {
		this.A = A;
		this.B = B;
		this.steps = steps;
	}
	
	public String toString() {
		return "[" + A + "], [" + B + "]";
	}
	
	/** 
	 * Gives all two stack crosses that yield two scorable stacks.
	 * Scorable stacks are:
	 * R R R R G
	 * R R G G G
	 * R G R G G
	 */
	public TwoStack[] getCrossOptions() {
		for (int i = 0; i <= 7; i++ ){
			TwoStack ts = cross(i);
			System.out.println(ts);
		}
		return null;
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
		AssemblyStep[] steps = stepsToCross(this, new TwoStack(a, b));
		return new TwoStack(a, b, steps);
	}
	
	public AssemblyStep[] stepsToCross(TwoStack src, TwoStack dest) {
		AssemblyStep[] allowedMoves = null;
		return null;
	}
}
