package mission;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import core.Config;
import rrt.PathPlanning;
import state_machine.game.PlannerState;
import map.Map;
import map.MapLoader;
import map.Pose;
import map.elements.Platform;
import map.elements.Stack;
import map.geom.Point;
import map.geom.Polygon;
import map.geom.Segment;
import mission.assmebly.Assembler;
import mission.assmebly.AssemblyStep;
import mission.gameplan.GameState;
import mission.gameplan.LocationState;
import mission.gameplan.LocationType;
import mission.gameplan.operations.GameOperation;
import mission.gameplan.operations.GrabPortOp;
import mission.gameplan.operations.MoveToLocationOp;

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
		Queue<GameState> open = new PriorityQueue<GameState>(50000, new Comparator<GameState>() {
			@Override
			public int compare(GameState o1, GameState o2) {
				return -o1.computeScore() + o2.computeScore();
			}
		});
		HashMap<String, Boolean> visited = new HashMap<String, Boolean>();

		open.add(start);
		GameState maximizedState = start;
		double maxScore = 0;
		int cnt = 0;

		while (open.size() > 0) {
			GameState current = open.poll();
			visited.put(current.toString(), true);
			
			if (cnt % 20000 == 0) {
				System.out.println(open.size() + " | " + visited.size() + " --- max = " + maxScore);
			}
			cnt++;
			if (cnt > 1000000) {
				break;
			}
			
			for (GameOperation gameOperation : current.getAllowedOps()) {
				GameState newState = current.apply(gameOperation);
				if (newState.timeRemaining <= 0) {
					continue;
				}
				if (newState.movesSinceLastScore > 3) {
					continue;
				}
				/*
				if (newState.timeRemaining < maximizedState.timeRemaining
				  && newState.computeScore() < maxScore) {
					continue;
				}*/
				double score = newState.computeScore() + newState.timeRemaining / (3 * 60 * 1000.0);
				if (score > maxScore) {
					maxScore = score;
					maximizedState = newState;
				}
				if (visited.containsKey(newState.toString())) {
					if (newState.timeRemaining > maximizedState.timeRemaining) {
						if (newState.toString().equals(maximizedState.toString())) {
							maxScore = newState.computeScore();
							maximizedState = newState;
						}
					}
					continue;
				}
				open.add(newState);
			}
		}
		
		System.out.println("Optimal Solution [SCORE = " + maxScore + "], STATE = " + maximizedState.toString());
		return backtrace(maximizedState);
	}
	
	public List<GameOperation> backtrace(GameState current) {
		List<GameOperation> ops = new ArrayList<GameOperation>();
		while (current != null) {
			ops.add(0, current.op);
			current = current.parent;
		}
		ops.remove(0);
		return ops;
	}
	
	public GameState createGameState(Map map, long timeRemaining) {
		List<LocationState> locationStates = new ArrayList<LocationState>();
		// Create robot location
		locationStates.add(new LocationState(new TwoStack("", ""), LocationType.START, new Pose(map.bot.pose.x, map.bot.pose.y, 0), 0));
		
		// Create stack locations
		Polygon poly = map.getHomeBase().getPolygon();
		for (Stack stack : map.getStacks()) {
			LocationType locType = LocationType.STACK;
			if (poly.contains(stack.pt)) {
				locType = LocationType.HOMEBASE;
			}
			Segment s = map.getBestApproach(stack.pt);
			s = s.trim(Config.HUB_DISTANCE);
			s = new Segment(s.end, s.start);
			Pose hub = new Pose(s.start.x, s.start.y, s.theta);
			locationStates.add(new LocationState(new TwoStack("", stack.cubes), locType, hub, 0));
		}
		
		// Create platform locations
		for (Platform platform : map.getPlatforms()) {
			Segment s = map.getBestPlatformApproach(platform);
			s = s.trim(Config.HUB_DISTANCE);
			s = new Segment(s.end, s.start);
			Pose hub = new Pose(s.start.x, s.start.y, s.theta);
			locationStates.add(new LocationState(new TwoStack("", ""), LocationType.PLATFORM, hub, 0));
		}
		
		// Find good point inside homebase
		Segment s = map.getBestHomePose();
		s = s.trim(Config.HUB_DISTANCE);
		s = new Segment(s.end, s.start);
		Pose hub = new Pose(s.start.x, s.start.y, s.theta);
		locationStates.add(new LocationState(new TwoStack("", ""), LocationType.HOMEBASE, hub, 0));
		return new GameState(0, "", locationStates, map, timeRemaining, null, null, 0);
	}
	
	public static void printPlanString(GameState gs, List<GameOperation> ops) {
		System.out.println(gs.toSettingsString());
		for (int i = 0; i < ops.size(); i++) {
			System.out.println(ops.get(i).toPlanString());
		}
	}
	
	public static void main(String[] args) {
		Map map = Map.getInstance();
		MapLoader.load(map, new File("gameMaps/practice_field.txt"));
		MissionPlanner mp = MissionPlanner.getInstance();
		GameState gs = mp.createGameState(map, (int)(3 * 60 * 1000));
		List<GameOperation> ops = mp.plan(gs);
		MissionPlanner.printPlanString(gs, ops);
		
		/*
		System.out.println(gs.toString());
		gs = gs.apply(new MoveToLocationOp(7));
		System.out.println(gs.toString());
		gs = gs.apply(new GrabPortOp(2));
		System.out.println(gs.toString());
		gs = gs.apply(new MoveToLocationOp(8));
		System.out.println(gs.toString());
		
		List<GameOperation> ops = gs.getAllowedOps();
		for (GameOperation op : ops) {
			System.out.println(" -----> " + op.toPlanString());
		}
		*/
		
		/*
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
		TwoStack src = new TwoStack("RRG", "");
		TwoStack[] combos = src.getReorderOptions();
		for (int i = 0; i < combos.length; i++) {
			System.out.println(combos[i]);
		}
		*/
		/*
		TwoStack src = new TwoStack("RGR", "GGR");
		TwoStack dest = new TwoStack("RRR", "GGG");
		AssemblyStep[] steps = Assembler.getAssemblySteps(src, dest);
		System.out.println("# of steps = " + steps.length);
		for (int i = 0; i < steps.length; i++) {
			System.out.println(steps[i].name());
		}
		*/
	}
}
