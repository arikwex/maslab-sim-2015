package state_machine;

import logging.RobotGraph;
import map.geom.Point;
import state_machine.game.AimState;
import state_machine.game.PlannerState;
import core.Config;

public class StateMachine {

	private static StateMachine instance;

	private State state;
	private Point goal;

	public StateMachine() {
	}

	public static StateMachine getInstance() {
		if (instance == null)
			instance = new StateMachine();
		return instance;
	}

	public void step() {
		if (state == null) {
			state = new AimState(null, new Point(3.0, 3.0));
		}
		state = state.step();
	}

	public void setGoal(Point p) {
		this.goal = p;
	}

	public void setPointer(Point p) {
		if (Config.sim) {
			RobotGraph.pointer = p;
		}
	}

	public Point getGoal() {
		return goal;
	}
}
