package mission.assmebly;

public enum AssemblyStep {
	// Cost estimates are in milliseconds
	// set grab position
	_BOT(500),
	_MID(500),
	_TOP(500),
	
	// Turn to position
	_T1(2500),
	_T2(2500),
	_T3(2500),
	
	// Estimated cost operation
	_DEPLOY(300),
	_GRAB(300);
	
	public int estimatedCost;
	
	private AssemblyStep(int cost) {
		estimatedCost = cost;
	}
}
