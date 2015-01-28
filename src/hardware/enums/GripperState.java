package hardware.enums;

public enum GripperState {
	OPEN(0),
	CLOSE(0.75);
	
	public double value = 0;
	
	private GripperState(double setting) {
		this.value = setting;
	}
}
