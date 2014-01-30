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
    public Cytron motor_left;
    public Cytron motor_right;
    public Servo servo_grip;
    public Servo servo_elevation;
    public DigitalOutput ball_launcher;
    public DigitalInput range_sensor;
    public Encoder encoder_left;
    public Encoder encoder_right;
    
    public Hardware() {
        // Initialize MapleComm
        comm = new MapleComm(MapleIO.SerialPortType.LINUX);
        
        // Initialize devices
        // TODO: Get actual pin numbers and servo types
        motor_left = new Cytron(Config.MOTOR_LEFT_DIR_PIN, Config.MOTOR_LEFT_PWM_PIN);
        motor_right = new Cytron(Config.MOTOR_RIGHT_DIR_PIN, Config.MOTOR_RIGHT_PWM_PIN);
        servo_grip = new Servo6001HB(Config.SERVO_GRIP_PIN);
        servo_elevation = new Servo6001HB(Config.SERVO_ELEVATION_PIN);
        ball_launcher = new DigitalOutput(Config.BALL_LAUNCHER_PIN);
        range_sensor = new DigitalInput(Config.RANGE_SENSOR_PIN);
        encoder_left = new Encoder(Config.ENCODER_LEFT_PIN_A, Config.ENCODER_LEFT_PIN_B);
        encoder_right = new Encoder(Config.ENCODER_RIGHT_PIN_A, Config.ENCODER_RIGHT_PIN_B);
        
        // Register devices and initialize Maple
        comm.registerDevice(motor_left);
        comm.registerDevice(motor_right);
        comm.registerDevice(servo_grip);
        comm.registerDevice(servo_elevation);
        comm.registerDevice(ball_launcher);
        comm.registerDevice(range_sensor);
        comm.registerDevice(encoder_left);
        comm.registerDevice(encoder_right);
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
