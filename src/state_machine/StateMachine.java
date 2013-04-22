package robot

public class StateMachine {
    private StateEstimation se;

    private State state;
    
    public StateMachine(StateEstimation se) {
        this.se = se;
        state = new ExploreState();
    }
    
    public void step() {
        state = state.step();
    }
}
