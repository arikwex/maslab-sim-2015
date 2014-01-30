package state_machine;

public class DoNothingState extends State {
    /* Class to just do nothing for now */
    public State transition() {
        return this;
    }
    public void run() {}
}
