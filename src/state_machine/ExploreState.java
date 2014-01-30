package state_machine;

import java.util.ArrayList;

import map.Point;
import vision.Ball;

public class ExploreState extends State {
    /* Class to just do nothing for now */
    public State transition() {
        /* If we find a ball in our FOV, transition to the TrackBallState,
         * tracking the closest ball we see.
         */
        /*
        Vision v = Vision.getInstance();
        ArrayList<Ball> balls = v.getBalls();
        Ball closest = getClosestBall(balls);
        if (closest != null) {
            return new TrackBallState(closest);
        }
        else {
            return this;
        }
        */
        return this;
    }
    
    private Ball getClosestBall(ArrayList<Ball> balls) {
        double smallest_r = Double.POSITIVE_INFINITY;
        Ball smallest = null;
        for (int i = 0; i < balls.size(); i++) {
            if (balls.get(i).r < smallest_r) {
                smallest = balls.get(i);
            }
        }
        return smallest;
    }
    
    public void run() {
    	StateMachine sm = StateMachine.getInstance();
    	sm.setGoal(new Point(0.5, 0.5));
    }
}