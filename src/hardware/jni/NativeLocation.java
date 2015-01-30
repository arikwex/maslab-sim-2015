package hardware.jni;

import hardware.components.Location;

public class NativeLocation implements Location {

	@Override
	public double getLocX() {
		return HardwareJNI.getPoseX();
	}

	@Override
	public double getLocY() {
		return HardwareJNI.getPoseY();
	}

	@Override
	public double getLocTheta() {
		return HardwareJNI.getPoseTheta();
	}

}
