package hardware;

import comm.MapleComm;
import comm.MapleIO;

import core.Config;
import devices.actuators.Cytron;
import devices.actuators.DigitalOutput;
import devices.actuators.PWMOutput;
import devices.actuators.Servo;
import devices.actuators.Servo6001HB;
import devices.sensors.DigitalInput;
import devices.sensors.Encoder;

public class Hardware {
    private static Hardware instance;
    
    private MapleComm comm;
    public Cytron motorLeft;
    public Cytron motorRight;
    public Servo servoGrip;
    public Servo servoElevation;
    public DigitalOutput ballLauncher;
    public DigitalInput rangeSensor;
    public Encoder encoderLeft;
    public Encoder encoderRight;
    
    public Hardware() {
        // Initialize MapleComm
        comm = new MapleComm(MapleIO.SerialPortType.WINDOWS);
        
        // Initialize devices
        // TODO: Get actual pin numbers and servo types
        motorLeft = new Cytron(Config.MOTOR_LEFT_DIR_PIN, Config.MOTOR_LEFT_PWM_PIN);
        motorRight = new Cytron(Config.MOTOR_RIGHT_DIR_PIN, Config.MOTOR_RIGHT_PWM_PIN);
        servoGrip = new Servo6001HB(Config.SERVO_GRIP_PIN);
        servoElevation = new Servo6001HB(Config.SERVO_ELEVATION_PIN);
        ballLauncher = new DigitalOutput(Config.BALL_LAUNCHER_PIN);
        rangeSensor = new DigitalInput(Config.RANGE_SENSOR_PIN);
        encoderLeft = new Encoder(Config.ENCODER_LEFT_PIN_A, Config.ENCODER_LEFT_PIN_B);
        encoderRight = new Encoder(Config.ENCODER_RIGHT_PIN_A, Config.ENCODER_RIGHT_PIN_B);
        
        // Register devices and initialize Maple
        comm.registerDevice(motorLeft);
        comm.registerDevice(motorRight);
        comm.registerDevice(servoGrip);
        comm.registerDevice(servoElevation);
        comm.registerDevice(ballLauncher);
        comm.registerDevice(rangeSensor);
        comm.registerDevice(encoderLeft);
        comm.registerDevice(encoderRight);
        comm.initialize();
    }
    
    public static Hardware getInstance() {
        if (instance == null) {
            instance = new Hardware();
        }
        return instance;
    }
    
    public void transmit() {
        comm.transmit();
    }
    public void updateSensorData() {
        comm.updateSensorData();
    }
}
