package hardware.simulated;

import hardware.components.Encoder;
import core.Config;

public class SimulatedEncoder implements Encoder {
	private long lastTime = System.currentTimeMillis();
	private SimulatedMotor motor = null;
	private double TICKS_PER_ROT = 190;

	public SimulatedEncoder(int pinA, int pinB) {
	}

	@Override
	public double getDeltaDistance() {
		long nowTime = System.currentTimeMillis();
		double dT = (nowTime - lastTime) / 1000.0;
		lastTime = nowTime;
		return motor.getSpeed() * dT * TICKS_PER_ROT * Config.WHEEL_RADIUS;
	}

	public void setSimulatedMotor(SimulatedMotor motor) {
		this.motor = motor;
	}
}
