package hardware.jni;

import hardware.components.Encoder;

public class NativeEncoder implements Encoder {

	private boolean left;
	private double lastDist;

	public NativeEncoder(boolean left) {
		this.left = left;
		this.lastDist = 0;
	}

	@Override
	public double getDeltaDistance() {
		double nextDist = left ? HardwareJNI.getLeftDistance() : HardwareJNI.getRightDistance();
		double deltaDist = this.lastDist - nextDist;
		this.lastDist = nextDist;
		return deltaDist;
	}

}
