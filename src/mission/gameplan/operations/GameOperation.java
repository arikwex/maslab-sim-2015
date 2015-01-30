package mission.gameplan.operations;

import mission.gameplan.GameState;

public abstract class GameOperation {
	private GameState parent;
	private GameOperation previous;
	
	// TODO: Is this necessary?
	public GameState getParent() {
		return parent;
	}
	
	public GameOperation getPrevious() {
		return previous;
	}
}
