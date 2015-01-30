package hardware.jni;

public class HardwareJNI {
	public static native void setLeftSpeed(double pwm);
	public static native void setRightSpeed(double pwm);
	public static native double getPoseX();
	public static native double getPoseY();
	public static native double getPoseTheta();
}
