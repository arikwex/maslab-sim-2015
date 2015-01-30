package hardware.jni;

import hardware.components.Motor;

public class NativeMotor implements Motor {
	private boolean left;

	public NativeMotor(boolean left) {
		this.left = left;
	}

	@Override
	public void setSpeed(double pwm) {
		if (left) {
			HardwareJNI.setLeftSpeed(pwm);
		} else {
			HardwareJNI.setRightSpeed(pwm);
		}
	}
}
