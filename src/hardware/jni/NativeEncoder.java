package hardware.jni;

import hardware.components.Encoder;
import hardware.simulated.SimulatedMotor;

public class NativeEncoder implements Encoder {
	private long lastTime = System.currentTimeMillis();
	private double deltaDistance = 0;
	private double velocity = 0;

	private boolean left;
	private double lastDist;

	public NativeEncoder(boolean left) {
		this.left = left;
		this.lastDist = 0;
	}

	@Override
	public void sample() {
		long nowTime = System.currentTimeMillis();
		double dT = (nowTime - lastTime) / 1000.0;

		double nextDist = left ? HardwareJNI.getLeftDistance() : HardwareJNI.getRightDistance();
		double deltaDist = this.lastDist - nextDist;

		this.deltaDistance = deltaDist;
		this.velocity = deltaDist / dT;

		this.lastDist = nextDist;
		this.lastTime = nowTime;
	}
	
	@Override
	public double getDeltaDistance() {
		return this.deltaDistance;
	}
	
	@Override
	public double getVelocity() {
		return this.velocity;
	}

}
