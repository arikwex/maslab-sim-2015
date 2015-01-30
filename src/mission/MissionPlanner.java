package mission;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import map.Map;
import map.MapLoader;
import map.Pose;
import map.elements.Platform;
import map.elements.Stack;
import map.geom.Point;
import map.geom.Polygon;
import map.geom.Segment;
import mission.gameplan.GameState;
import mission.gameplan.LocationState;
import mission.gameplan.LocationType;
import mission.gameplan.operations.GameOperation;

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
		Queue<GameState> open = new PriorityQueue<GameState>(0, new Comparator<GameState>() {
			@Override
			public int compare(GameState o1, GameState o2) {
				return -o1.computeScore() + o2.computeScore();
			}
		});
		HashMap<String, GameState> visited = new HashMap<String, GameState>();

		open.add(start);
		GameState maximizedState = start;
		int maxScore = 0;
		int cnt = 0;

		while (open.size() > 0) {
			GameState current = open.poll();
			visited.put(current.toString(), current);
			if (cnt < 0) {
				System.out.println(open.size() + " | " + visited.size() + " --- current = " + current.computeScore());
				cnt = 10000;
			} else {
				cnt--;
			}
			//System.out.println("{t = " + current.timeRemaining + "} CURRENT ===== " + current);
			
			for (GameOperation gameOperation : current.getAllowedOps()) {
				GameState newState = current.apply(gameOperation);
				if (newState.timeRemaining < 0) {
					continue;
				}
				if (newState.timeRemaining < 100000 && newState.computeScore() == 0) {
					continue;
				}
				if (visited.containsKey(newState.toString())) {
					continue;
				}
				int score = newState.computeScore();
				//System.out.println("["+score+"] " + newState);
				if (score > maxScore) {
					maxScore = score;
					maximizedState = newState;
				}
				open.add(newState);
			}
			
			try {
				//Thread.sleep(5);
			} catch (Exception e) {
			}
		}
		
		System.out.println("Optimal Solution [SCORE = " + maxScore + "], STATE = " + maximizedState.toString());
		return null;
	}
	
	public GameState createGameState(Map map, long timeRemaining) {
		List<LocationState> locationStates = new ArrayList<LocationState>();
		// Create robot location
		locationStates.add(new LocationState(new TwoStack("", ""), LocationType.START, new Pose(map.bot.pose.x, map.bot.pose.y, 0)));
		
		// Create stack locations
		Polygon poly = map.getHomeBase().getPolygon();
		for (Stack stack : map.getStacks()) {
			LocationType locType = LocationType.STACK;
			if (poly.contains(stack.pt)) {
				locType = LocationType.HOMEBASE;
			}
			locationStates.add(new LocationState(new TwoStack("", stack.cubes), locType, new Pose(stack.pt.x, stack.pt.y, 0)));
		}
		
		// Create platform locations
		for (Platform platform : map.getPlatforms()) {
			locationStates.add(new LocationState(new TwoStack("", ""), LocationType.PLATFORM, platform.getDockingPose()));
		}
		
		// Find random points inside homebase
		for (int i = 0; i < 2; i++) {
			// TODO: This is horrible.
			Point hb = map.randomPoint();
			Segment seg = new Segment(new Point(1, 1), new Point(0, 0));
			while (poly.contains(hb) && !map.checkSegment(seg, 0)) {
				hb = map.randomPoint();
			}
			locationStates.add(new LocationState(new TwoStack("", ""), LocationType.HOMEBASE, new Pose(hb.x, hb.y, 0)));
		}
		return new GameState(0, "", locationStates, map, timeRemaining, null, null);
	}
	
	public static void main(String[] args) {
		Map map = Map.getInstance();
		MapLoader.load(map, new File("gameMaps/practice_field.txt"));
		MissionPlanner mp = MissionPlanner.getInstance();
		
		GameState gs = mp.createGameState(map, 3 * 60 * 1000);
		mp.plan(gs);
		//gs.getAllowedOps();
		
		/*
		System.out.println(gs.toString());
		gs = gs.apply(new MoveToLocationOp(2));
		System.out.println(gs.toString());
		gs = gs.apply(new GrabPortOp(2));
		System.out.println(gs.toString());
		gs = gs.apply(new MoveToLocationOp(5));
		System.out.println(gs.toString());
		gs = gs.apply(new DeployPortOp(1));
		System.out.println(gs.toString());
		gs = gs.apply(new AssembleOp(new TwoStack("GGR","GRR")));
		System.out.println(gs.toString());
		gs = gs.apply(new GrabPortOp(1));
		System.out.println(gs.toString());
		gs = gs.apply(new MoveToLocationOp(6));
		System.out.println(gs.toString());
		gs = gs.apply(new DeployPlatformOp());
		System.out.println(gs.toString());
		System.out.println(gs.computeScore());
		*/
		
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
