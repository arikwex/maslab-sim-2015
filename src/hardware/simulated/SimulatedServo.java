package hardware.simulated;

import hardware.components.Servo;

public class SimulatedServo implements Servo {
	private double value = 0;
	protected double desiredValue = 0;
	public SimulatedServo(int pin) {
		Thread t = new Thread() {
			public void run() {
				while (true) {
					value += (desiredValue - value) * 0.2;
					try {
						Thread.sleep(20);
					} catch (Exception e) {
					}
				}
			}
		};
		t.start();
	}
	
	public double getTransientValue() {
		return this.value;
	}
	
	public void setValue(double v) {
		this.desiredValue = v;
	}
	
	public double getValue() {
		return this.desiredValue;
	}
}
