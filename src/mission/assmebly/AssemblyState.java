package mission.assmebly;

import java.util.ArrayList;
import java.util.List;

import mission.TwoStack;

public class AssemblyState {
	private final String A, B, C;
	private final int port;
	private final int arm;
	private final String holding;
	private final int cost;
	private final AssemblyStep previous;
	private final AssemblyState parent;
	
	public AssemblyState(String A, String B, String C) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.port = 0;
		this.arm = 0;
		this.holding = "";
		this.cost = 0;
		this.parent = null;
		this.previous = null;
	}
	
	public AssemblyState(String A, String B, String C, int port, int arm, String holding, int cost, AssemblyState parent, AssemblyStep previous) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.port = port;
		this.arm = arm;
		this.holding = holding;
		this.cost = cost;
		this.parent = parent;
		this.previous = previous;
	}
	
	public String toString() {
		return "State: [" + A + ", " + B +", " + C +"], port: " + port +", arm: " + arm +", holding: [" + holding + "]";
	}
	
	public AssemblyState apply(AssemblyStep asm) {
		if (asm == AssemblyStep._BOT) {
			return new AssemblyState(A, B, C, port, 1, holding, cost + AssemblyStep._BOT.estimatedCost, this, asm);
		} else if (asm == AssemblyStep._MID) {
			return new AssemblyState(A, B, C, port, 2, holding, cost + AssemblyStep._MID.estimatedCost, this, asm);
		} else if (asm == AssemblyStep._TOP) {
			return new AssemblyState(A, B, C, port, 3, holding, cost + AssemblyStep._TOP.estimatedCost, this, asm);
		} else if (asm == AssemblyStep._GRAB) {
			return grab();
		} else if (asm == AssemblyStep._DEPLOY) {
			return deploy();
		} else if (asm == AssemblyStep._T1) {
			return new AssemblyState(A, B, C, 1, arm, holding, cost + AssemblyStep._T1.estimatedCost, this, asm);
		} else if (asm == AssemblyStep._T2) {
			return new AssemblyState(A, B, C, 2, arm, holding, cost + AssemblyStep._T2.estimatedCost, this, asm);
		} else if (asm == AssemblyStep._T3) {
			return new AssemblyState(A, B, C, 3, arm, holding, cost + AssemblyStep._T3.estimatedCost, this, asm);
		}
		return null;
	}
	
	public AssemblyState grab() {
		int num = arm-1;
		if (port == 1) {
			return new AssemblyState(A.substring(0, num), B, C, port, arm, A.substring(num), cost + AssemblyStep._GRAB.estimatedCost, this, AssemblyStep._GRAB);
		} else if (port == 2) {
			return new AssemblyState(A, B.substring(0, num), C, port, arm, B.substring(num), cost + AssemblyStep._GRAB.estimatedCost, this, AssemblyStep._GRAB);
		} else if (port == 3) {
			return new AssemblyState(A, B, C.substring(0, num), port, arm, C.substring(num), cost + AssemblyStep._GRAB.estimatedCost, this, AssemblyStep._GRAB);
		}
		return null;
	}
	
	public AssemblyState deploy() {
		if (port == 1) {
			return new AssemblyState(A + holding, B, C, port, arm, "", cost + AssemblyStep._DEPLOY.estimatedCost, this, AssemblyStep._DEPLOY);
		} else if (port == 2) {
			return new AssemblyState(A, B + holding, C, port, arm, "", cost + AssemblyStep._DEPLOY.estimatedCost, this, AssemblyStep._DEPLOY);
		} else if (port == 3) {
			return new AssemblyState(A, B, C + holding, port, arm, "", cost + AssemblyStep._DEPLOY.estimatedCost, this, AssemblyStep._DEPLOY);
		}
		return null;
	}
	
	public AssemblyStep[] getAllowedSteps() {
		// If no hub port OR arm position have been set
		if (port == 0 || arm == 0) {
			return new AssemblyStep[]{
				AssemblyStep._T1, AssemblyStep._T2, AssemblyStep._T3,
				AssemblyStep._BOT, AssemblyStep._MID, AssemblyStep._TOP
			};
		}
		
		// If no cubes are being held
		if (holding.length() == 0) {
			int cubesOnFloor = 0;
			if (port == 1) {
				cubesOnFloor = A.length();
			} else if (port == 2) {
				cubesOnFloor = B.length();
			} else if (port == 3) {
				cubesOnFloor = C.length();
			}
			List<AssemblyStep> allowed = new ArrayList<AssemblyStep>();
			allowed.add(AssemblyStep._T1);
			allowed.add(AssemblyStep._T2);
			allowed.add(AssemblyStep._T3);
			
			if (cubesOnFloor >= 1) {
				allowed.add(AssemblyStep._TOP);
			}
			if (cubesOnFloor >= 2) {
				allowed.add(AssemblyStep._MID);
			}
			if (cubesOnFloor == 3) {
				allowed.add(AssemblyStep._BOT);
			}
				
			if (arm <= cubesOnFloor) {
				allowed.add(AssemblyStep._GRAB);
			}
			return allowed.toArray(new AssemblyStep[allowed.size()]);
		} else {
			int cubesOnFloor = 0;
			int cubesHeld = holding.length();
			if (port == 1) {
				cubesOnFloor = A.length();
			} else if (port == 2) {
				cubesOnFloor = B.length();
			} else if (port == 3) {
				cubesOnFloor = C.length();
			}
			List<AssemblyStep> allowed = new ArrayList<AssemblyStep>();
			allowed.add(AssemblyStep._T1);
			allowed.add(AssemblyStep._T2);
			allowed.add(AssemblyStep._T3);
			
			if (cubesOnFloor == 0) {
				allowed.add(AssemblyStep._BOT);
			} else if (cubesOnFloor == 1) {
				allowed.add(AssemblyStep._MID);
			} else if (cubesOnFloor == 2) {
				allowed.add(AssemblyStep._BOT);
			}
				
			if ((arm == cubesOnFloor + 1) && (cubesOnFloor + cubesHeld <= 3)) {
				allowed.add(AssemblyStep._DEPLOY);
			}
			return allowed.toArray(new AssemblyStep[allowed.size()]);
		}
	}
	
	public TwoStack toTwoStack() {
		return new TwoStack(A, B);
	}
	
	public int getCost() {
		return cost;
	}
	
	public AssemblyState getParent() {
		return parent;
	}
	
	public AssemblyStep getStep() {
		return previous;
	}
}
