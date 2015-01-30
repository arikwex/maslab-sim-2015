package hardware.simulated;

import hardware.components.Motor;

public class SimulatedMotor implements Motor {
	private double pwm;
	private boolean dir;
	private double speed;
	private final double MAX_RPM = 120;
	
	public SimulatedMotor(int dir, int pwm) {
	}
	
	public void setSpeed(double pwm) {
		if (pwm > 0) {
			dir = true;
		} else {
			dir = false;
		}
		if (pwm < 0) {
			pwm = -pwm;
		}
		if (pwm > 1) {
			pwm = 1;
		}
		this.pwm = pwm;
		
		this.speed = speedMapping(dir, pwm);
	}
	
	public double speedMapping(boolean dir, double pwm) {
		double power = Math.pow(pwm, 3);
		if (!dir) {
			power *= -1;
		}
		return power;
	}
	
	public double getSpeed() {
		return speed * MAX_RPM;
	}
}
