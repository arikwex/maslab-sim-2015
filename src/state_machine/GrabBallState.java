package state_machine;

public class GrabBallState extends State {

    private boolean done = false;
    
    @Override
    protected State transition() {
        if (done) {
            return new ExploreState();
        }
        else {
            return this;
        }
    }

    @Override
    protected void run() {
        // TODO: Fill in grab ball steps
    }
}
