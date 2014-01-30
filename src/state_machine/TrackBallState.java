package state_machine;

import hardware.Hardware;
import vision.Ball;

public class TrackBallState extends State {

    private Ball b;
    private Hardware hw;
    
    public TrackBallState(Ball b) {
        this.b = b;
        this.hw = Hardware.getInstance();
    }
    
    @Override
    protected State transition() {
        hw.updateSensorData();
        // Range sensor detects ball
        if (!hw.range_sensor.getValue()) {
            return new GrabBallState();
        }
        else{
            return this;
        }
    }

    @Override
    protected void run() {
        
    }

}
