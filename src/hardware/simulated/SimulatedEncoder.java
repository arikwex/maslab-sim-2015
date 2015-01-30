package hardware.simulated;

import hardware.components.Encoder;
import core.Config;

public class SimulatedEncoder implements Encoder {
	private long lastTime = System.currentTimeMillis();
	private SimulatedMotor motor = null;
	
	public SimulatedEncoder(int pinA, int pinB) {
	}

	@Override
	public double getDeltaDistance() {
		long nowTime = System.currentTimeMillis();
		double dT = (nowTime - lastTime) / 1000.0;
		lastTime = nowTime;
		return motor.getSpeed() * dT / 20.0;
	}

	public void setSimulatedMotor(SimulatedMotor motor) {
		this.motor = motor;
	}
}
