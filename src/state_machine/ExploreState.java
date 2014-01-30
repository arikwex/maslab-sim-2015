package state_machine;

import java.util.ArrayList;

import map.Point;
import vision.Ball;
import core.StateEstimator;

public class ExploreState extends State {
	
	private Point curGoal;
	private int reactorIndex = 0;
	
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
    	StateEstimator se = StateEstimator.getInstance();
    	
    	if (curGoal == null) {
    		curGoal = se.map.reactors.get(reactorIndex);
    	}
    	else if (curGoal.distance(new Point(se.map.bot.pose.x, se.map.bot.pose.y)) < 0.3) {
    		reactorIndex++;
    		curGoal = se.map.reactors.get(reactorIndex);
    	}
    		
    	sm.setGoal(curGoal);
    }
}