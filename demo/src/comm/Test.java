package comm;


import devices.actuators.Cytron;
import devices.actuators.DigitalOutput;
import devices.actuators.Servo;
import devices.actuators.Servo1800A;
import devices.actuators.Servo3001HB;
import devices.actuators.Servo6001HB;
import devices.sensors.ColorSensor;
import devices.sensors.DigitalInput;
import devices.sensors.Encoder;
import devices.sensors.Gyroscope;
import devices.sensors.Ultrasonic;

public class Test {
	
	private static final double GRAB_SERVO_CLOSE = 85;
	private static final double GRAB_SERVO_OPEN = 150;
	private static final double LIFT_SERVO_LOW = 160;
	private static final double LIFT_SERVO_HIGH = 90;

	public static void main(String[] args) throws InterruptedException {
		new Test();
		System.exit(0);
	}

	public Test() throws InterruptedException {
		
		/*
		 * Create your Maple communication framework by specifying what kind of 
		 * serial port you would like to try to autoconnect to.
		 */
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		/*
		 * Create an object for each device. The constructor arguments specify
		 * their pins (or, in the case of the gyroscope, the index of a fixed
		 * combination of pins).
		 * Devices are generally either Sensors or Actuators. For example, a
		 * motor controller is an actuator, and an encoder is a sensor.
		 */
		Servo grabServo = new Servo1800A(12);
		Servo liftServo = new Servo6001HB(11);
		DigitalInput ballIR = new DigitalInput(37);
		DigitalOutput solenoid = new DigitalOutput(36);
		Encoder leftEncoder = new Encoder(29, 30);
		Encoder rightEncoder = new Encoder(31, 32);
		Cytron leftMotor = new Cytron(8, 7);
		Cytron rightMotor = new Cytron(10, 9);

		/*
		 * Build up a list of devices that will be sent to the Maple for the
		 * initialization step.
		 */
		comm.registerDevice(grabServo);
		comm.registerDevice(liftServo);
		comm.registerDevice(ballIR);
		comm.registerDevice(solenoid);
		comm.registerDevice(leftEncoder);
		comm.registerDevice(rightEncoder);
		comm.registerDevice(leftMotor);
		comm.registerDevice(rightMotor);

		// Send information about connected devices to the Maple
		comm.initialize();

		liftServo.setAngle(LIFT_SERVO_LOW);
		grabServo.setAngle(GRAB_SERVO_OPEN);
		solenoid.setValue(true);
		comm.transmit();
		
		while (true) {
			comm.updateSensorData();
			
			/*
			if (!ballIR.getValue()) {
				grabServo.setAngle(GRAB_SERVO_CLOSE);
//				liftServo.setAngle(LIFT_SERVO_LOW);
				comm.transmit();
				Thread.sleep(1000);
				
//				grabServo.setAngle(GRAB_SERVO_CLOSE);
				liftServo.setAngle(LIFT_SERVO_HIGH);
				comm.transmit();
				Thread.sleep(1000);
				
				grabServo.setAngle(GRAB_SERVO_OPEN);
//				liftServo.setAngle(LIFT_SERVO_HIGH);
				comm.transmit();
				Thread.sleep(1000);
				
//				grabServo.setAngle(GRAB_SERVO_OPEN);
				liftServo.setAngle(LIFT_SERVO_LOW);
				comm.transmit();
			}
			
			Thread.sleep(20);
			*/
			
			/*
			solenoid.setValue(false);
			comm.transmit();
			Thread.sleep(35);
			solenoid.setValue(true);
			comm.transmit();
			Thread.sleep(600);
			*/
			
			System.out.println("LEFT: " + leftEncoder.getTotalAngularDistance());
			System.out.println("RIGHT: " + rightEncoder.getTotalAngularDistance());
			
			leftMotor.setSpeed(1);
			rightMotor.setSpeed(1);
			comm.transmit();
			
			Thread.sleep(100);
		}
	}
}
