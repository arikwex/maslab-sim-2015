package state_machine;

public class ExploreState extends State{
    
    public ExploreState(StateMachine sm) {
        super(sm);
    }

    protected State transition() {
        return this;
    }
    
    protected void run() {
        this.machine.setGoal(this.machine.se.getClosestBlock());
    }
}
