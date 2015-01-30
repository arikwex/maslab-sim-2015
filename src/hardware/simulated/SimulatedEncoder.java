package hardware.simulated;

import hardware.components.Encoder;

public class SimulatedEncoder implements Encoder {
	private long lastTime = System.currentTimeMillis();
	private SimulatedMotor motor = null;
	private double deltaDistance = 0;
	private double velocity = 0;
	
	public SimulatedEncoder(int pinA, int pinB) {
	}
	
	@Override
	public void sample() {
		long nowTime = System.currentTimeMillis();
		double dT = (nowTime - lastTime) / 1000.0;
		lastTime = nowTime;
		this.deltaDistance = motor.getSpeed() * dT;
		this.velocity = motor.getSpeed();
	}
	
	@Override
	public double getDeltaDistance() {
		return this.deltaDistance;
	}
	
	@Override
	public double getVelocity() {
		return this.velocity;
	}

	public void setSimulatedMotor(SimulatedMotor motor) {
		this.motor = motor;
	}


}
