package hardware.enums;

public enum ElevatorState {
	BOTTOM(0),
	MIDDLE(0.25),
	TOP(0.5),
	TRANSIT(0.75);
	
	public double value = 0;
	
	private ElevatorState(double setting) {
		this.value = setting;
	}
}
