package mission;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import map.Map;
import map.MapLoader;
import map.Pose;
import map.elements.Platform;
import map.elements.Stack;
import map.geom.Point;
import map.geom.Polygon;
import map.geom.Segment;
import mission.gameplan.GameOperation;
import mission.gameplan.GameState;
import mission.gameplan.LocationState;
import mission.gameplan.LocationType;

public class MissionPlanner {
	public static MissionPlanner mp = null;
	
	public MissionPlanner() {
	}
	
	public static MissionPlanner getInstance() {
		if (mp == null) {
			mp = new MissionPlanner();
		}
		return mp;
	}
	
	public TwoStack mergeTwoStack(TwoStack twoStack) {
		return null;
	}
	
	public List<GameOperation> plan(GameState start) {
		Queue<GameState> open = new LinkedList<GameState>();
		
		open.add(start);
		GameState maximizedState = start;
		int maxScore = 0;
		
		while (open.size() > 0 ) {
			GameState current = open.poll();
			for (GameOperation gameOperation : current.getAllowedOps()) {
				GameState newState = gameState.apply(gameOperation);
				int score = newState.computeScore();
				if (score > maxScore) {
					maxScore = score;
					maximizedState = newState;
				}
				open.add(newState);
			}
		}
		return null;
	}
	
	public GameState createGameState(Map map, long timeRemaining) {
		List<LocationState> locationStates = new ArrayList<LocationState>();
		// Create stack locations
		for (Stack stack : map.getStacks()) {
			locationStates.add(new LocationState(stack.cubes, LocationType.STACK, new Pose(stack.pt.x, stack.pt.y, 0)));
		}
		// Create platform locations
		for (Platform platform : map.getPlatforms()) {
			locationStates.add(new LocationState("", LocationType.PLATFORM, platform.getDockingPose()));
		}
		// Find random points inside homebase
		for (int i = 0; i < 2; i++) {
			Point hb = new Point(-10000, -10000);
			Polygon poly = map.getHomeBase().getPolygon();
			Segment seg = new Segment(new Point(1, 1), new Point(0, 0));
			while (!poly.contains(hb) && !map.checkSegment(seg, 0)) {
				hb = map.randomPoint();
			}
			locationStates.add(new LocationState("", LocationType.HOMEBASE, new Pose(hb.x, hb.y, 0)));
		}
		return new GameState(locationStates, map, timeRemaining, null, null);
	}
	
	public static void main(String[] args) {
		Map map = Map.getInstance();
		MapLoader.load(map, new File("gameMaps/practice_field.txt"));
		MissionPlanner mp = MissionPlanner.getInstance();
		
		GameState gs = mp.createGameState(map, 3 * 60 * 1000);
		//mp.plan(gs);
		
		/*
		TwoStack src = new TwoStack("RRR", "GRG");
		TwoStack dest = new TwoStack("RRR", "GGG");
		AssemblyStep[] steps = Assembler.getAssemblySteps(src, dest);
		System.out.println("# of steps = " + steps.length);
		for (int i = 0; i < steps.length; i++) {
			System.out.println(steps[i].name());
		}
		*/
	}
}
