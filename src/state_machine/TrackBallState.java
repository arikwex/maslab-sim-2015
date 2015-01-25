package state_machine;

import hardware.Hardware;

public class TrackBallState extends State {

    private Hardware hw;
    
    public TrackBallState() {
        this.hw = Hardware.getInstance();
    }
    
    @Override
    protected State transition() {
        return this;
    }

    @Override
    protected void run() {
        
    }

}
