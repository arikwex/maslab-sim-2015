package control;

public class PID {
	double kp, ki, kd, maxi, max;
	double prevError;
	long prevTime;
	double target;
	double accumI;

	double output;
	boolean running;

	/**
	 * Constructs new PID object with specified gains
	 * 
	 * @param kp - proportional gain
	 * @param ki - integral gain (note time is in seconds)
	 * @param kd - differential gain (note time is in seconds)
	 * @param maxi - maximum windup on i (pre-gain)
	 * @param max - maximum output value
	 */
	public PID(double kp, double ki, double kd, double maxi, double max) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.maxi = maxi;
		this.max = max;
		this.running = false;
	}

	/**
	 * Sets initial state of PID controller
	 * 
	 * @param value - used to calculate initial error (initialize prevError)
	 * @param target - also used to calculate initial error (initialize prevError), and set the PID target value
	 */
	public void start(double value, double target) {
		this.prevError = value - target;
		this.prevTime = System.currentTimeMillis();
		this.target = target;
		this.accumI = 0;
		this.running = true;
	}

	public void stop() {
		this.running = false;
	}

	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Performs PID control for one step by calculating each component and returning the sum
	 * 
	 * @param value - sensor input value
	 * @return - sum of proportional, integral, and differential control calculations
	 */
	public double step(double value) {
		double currError = target - value;
		long currTime = System.currentTimeMillis();
		double deltaT = (currTime - prevTime) / 1000.0;

		double p = kp * currError;

		//System.out.println("currError: " + currError);
		//System.out.println("deltaT: " + deltaT);

		this.accumI += currError * deltaT;
		if (accumI > maxi) {
			accumI = maxi;
		} else if (accumI < -maxi) {
			accumI = -maxi;
		}
		double i = ki * accumI;

		double d = 0;
		if (deltaT > 0)
			d = kd * (currError - prevError) / deltaT;
		
		//System.out.println("accumI: " + accumI);
		//System.out.println(p + ", " + i + ", " + d);
		double result = p + i + d;
		if (result > max) {
			result = max;
		} else if (result < -max) {
			result = -max;
		}

		this.output = result;

		this.prevError = currError;
		this.prevTime = currTime;

		return result;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public void setGains(double kp, double ki, double kd) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
	}

	public double getPGain() {
		return this.kp;
	}

	public double getIGain() {
		return this.ki;
	}

	public double getDGain() {
		return this.kd;
	}

	public double getOutput() {
		return output;
	}
	
	public static void main(String[] args) {
		PID pid = new PID(1, 0, 0.001, 1, 2);
		pid.start(0, 10);
		double actual = 0;
		for (int i = 0; i<20; i++) {
			sleep(10);
			System.out.println(actual);
			actual += pid.step(actual);
		}
	}
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
