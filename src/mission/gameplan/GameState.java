package mission.gameplan;

import java.util.List;

import map.Map;

public class GameState {
	public final Map map;
	public List<LocationState> locationStates;
	public final long timeRemaining;
	public final GameState parent;
	public final GameOperation op;
	
	public GameState(List<LocationState> locationStates, Map map, long timeRemaining, GameState parent, GameOperation op) {
		this.locationStates = locationStates;
		this.map = map;
		this.timeRemaining = timeRemaining;
		this.parent = parent;
		this.op = op;
	}
}
