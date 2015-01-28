package hardware;

import hardware.components.Camera;
import hardware.components.Motor;
import hardware.components.Encoder;
import hardware.components.Gyro;
import hardware.components.Lidar;
import hardware.components.Servo;
import hardware.simulated.SimulatedElevator;
import hardware.simulated.SimulatedGripper;
import hardware.simulated.SimulatedMotor;
import hardware.simulated.SimulatedEncoder;
import hardware.simulated.SimulatedServo;
import core.Config;

public class Hardware {
    private static Hardware instance;
    
    public Motor motorLeft;
    public Motor motorRight;
    public Servo servoGrip;
    public Servo servoElevation;
    public Encoder encoderLeft;
    public Encoder encoderRight;
    public Gyro gyroscope;
    public Lidar lidar;
    public Camera camera;
    
    public Hardware() {
        // Initialize devices
        motorLeft = new SimulatedMotor(Config.MOTOR_LEFT_DIR_PIN, Config.MOTOR_LEFT_PWM_PIN);
        motorRight = new SimulatedMotor(Config.MOTOR_RIGHT_DIR_PIN, Config.MOTOR_RIGHT_PWM_PIN);
        servoGrip = new SimulatedGripper(Config.SERVO_GRIP_PIN);
        servoElevation = new SimulatedElevator(Config.SERVO_ELEVATION_PIN);
        encoderLeft = new SimulatedEncoder(Config.ENCODER_LEFT_PIN_A, Config.ENCODER_LEFT_PIN_B);
        encoderRight = new SimulatedEncoder(Config.ENCODER_RIGHT_PIN_A, Config.ENCODER_RIGHT_PIN_B);
        
        // SIMULATION ONLY
        ((SimulatedEncoder)encoderLeft).setSimulatedMotor((SimulatedMotor)motorLeft);
        ((SimulatedEncoder)encoderRight).setSimulatedMotor((SimulatedMotor)motorRight);
    }
    
    public static Hardware getInstance() {
        if (instance == null) {
            instance = new Hardware();
        }
        return instance;
    }
}
