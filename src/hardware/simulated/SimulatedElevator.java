package hardware.simulated;

public class SimulatedElevator extends SimulatedServo {
	public SimulatedElevator(int pin) {
		super(pin);
	}
	
	public void setValue(double v) {
		desiredValue = v;
	}
}
