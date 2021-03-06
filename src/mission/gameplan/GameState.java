package mission.gameplan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import core.Config;
import rrt.PathPlanning;
import state_machine.game.PlannerState;
import map.Map;
import map.Pose;
import map.geom.Point;
import mission.TwoStack;
import mission.assmebly.Assembler;
import mission.assmebly.AssemblyStep;
import mission.gameplan.operations.AssembleOp;
import mission.gameplan.operations.DeployPlatformOp;
import mission.gameplan.operations.DeployPortOp;
import mission.gameplan.operations.GameOperation;
import mission.gameplan.operations.GrabPortOp;
import mission.gameplan.operations.MoveToLocationOp;

public class GameState {
	public final Map map;
	public final int robotLocation;
	public final String heldStack;
	public final List<LocationState> locationStates;
	public final long timeRemaining;
	public final GameState parent;
	public final GameOperation op;
	public final long movesSinceLastScore;
	public int stacksThatScore = 0;
	private int score;
	private int numZones;
	
	public GameState(int robotLocation, String heldStack,
					 List<LocationState> locationStates, Map map,
					 long timeRemaining, GameState parent, GameOperation op, long movesSinceLastScore) {
		this.robotLocation = robotLocation;
		this.heldStack = heldStack;
		this.locationStates = cloneLocations(locationStates);
		this.map = map;
		this.timeRemaining = timeRemaining;
		this.parent = parent;
		this.op = op;
		this.movesSinceLastScore = movesSinceLastScore;
		this.cacheScore();
	}
	
	public List<LocationState> cloneLocations(List<LocationState> ls) {
		List<LocationState> ret = new ArrayList<LocationState>();
		for (LocationState location : ls) {
			TwoStack clonedTwoStack = new TwoStack(location.twoStack.A, location.twoStack.B);
			ret.add(new LocationState(clonedTwoStack, location.type,
					new Pose(location.pose.x, location.pose.y, location.pose.theta), location.visited));
		}
		return ret;
	}
	
	public String toString() {
		String holding = "{" + heldStack + "}";
		String stacks = "";
		for (LocationState ls : locationStates) {
			if (ls.type == LocationType.START) {
				continue;
			}
			stacks += ls.toString() + "(" + ls.type.name() + ") ";
		}
		return robotLocation + " --> " + holding + ", " + stacks;
	}
	
	public GameState apply(GameOperation op) {
		if (op instanceof MoveToLocationOp) {
			// MOVE TO LOCATION
			MoveToLocationOp cast = (MoveToLocationOp)op;
			return move(cast);
		} else if (op instanceof GrabPortOp) {
			// GRAB ALL FROM Port
			GrabPortOp cast = (GrabPortOp)op;
			return grab(cast);
		} else if (op instanceof DeployPortOp) {
			// DEPLOY ALL AT Port
			DeployPortOp cast = (DeployPortOp)op;
			return deploy(cast);
		} else if (op instanceof DeployPlatformOp) {
			// DEPLOY ALL AT PLATFORM
			DeployPlatformOp cast = (DeployPlatformOp)op;
			return deploy(cast);
		} else if (op instanceof AssembleOp) {
			// ASSEMBLE
			AssembleOp cast = (AssembleOp)op;
			return assemble(cast);
		}
		return null;
	}
	
	public GameState move(MoveToLocationOp op) {
		Pose src = this.locationStates.get(robotLocation).pose;
		src = new Pose(src.x, src.y, 0);
		Pose dest = this.locationStates.get(op.loc).pose;
		dest = new Pose(dest.x, dest.y, 0);
		List<LocationState> locs = cloneLocations(locationStates);
		LocationState original = locs.get(op.loc);
		locs.set(op.loc, new LocationState(original.twoStack, original.type, original.pose, original.visited + 1));
		return new GameState(op.loc, heldStack, locationStates,
							 map, timeRemaining -  MOVE_ESTIMATE(src, dest), this, op, movesSinceLastScore + 1);
	}
	
	public GameState grab(GrabPortOp op) {
		String holding = "";
		TwoStack newStack = new TwoStack("", "");
		int port = op.port;
		LocationState robLoc = locationStates.get(robotLocation);
		if (port == 1) {
			holding = robLoc.twoStack.A;
			newStack = new TwoStack("", robLoc.twoStack.B);
		} else if (port == 2) {
			holding = robLoc.twoStack.B;
			newStack = new TwoStack(robLoc.twoStack.A, "");
		}
		List<LocationState> locs = cloneLocations(locationStates);
		locs.get(robotLocation).twoStack = newStack;
		return new GameState(robotLocation, holding, locs,
		 		   			 map, timeRemaining - GRAB_ESTIMATE(), this, op, movesSinceLastScore + 1);
	}
	
	public GameState deploy(DeployPortOp op) {
		TwoStack newStack = new TwoStack("", "");
		int port = op.port;
		LocationState robLoc = locationStates.get(robotLocation);
		if (port == 1) {
			newStack = new TwoStack(heldStack, robLoc.twoStack.B);
		} else if (port == 2) {
			newStack = new TwoStack(robLoc.twoStack.A, heldStack);
		}
		List<LocationState> locs = cloneLocations(locationStates);
		locs.get(robotLocation).twoStack = newStack;
		GameState newGameState = new GameState(robotLocation, "", locs,
		 		   			 	 map, timeRemaining - DEPLOY_ESTIMATE(), this, op, 0);
		if (newGameState.computeScore() < score) {
			newGameState = new GameState(robotLocation, "", locs,
	   			 	       map, timeRemaining - DEPLOY_ESTIMATE(), this, op, movesSinceLastScore + 1);
		}
		return newGameState;
	}
	
	public GameState deploy(DeployPlatformOp op) {
		List<LocationState> locs = cloneLocations(locationStates);
		locs.get(robotLocation).twoStack = new TwoStack("", heldStack);
		GameState newGameState = new GameState(robotLocation, "", locs,
   			 	  				 map, timeRemaining - DEPLOY_ESTIMATE(), this, op, 0);
		if (newGameState.computeScore() < score) {
			newGameState = new GameState(robotLocation, "", locs,
			 	           map, timeRemaining - DEPLOY_ESTIMATE(), this, op, movesSinceLastScore + 1);
		}
		return newGameState;
	}
	
	public GameState assemble(AssembleOp op) {
		List<LocationState> locs = cloneLocations(locationStates);
		TwoStack src = new TwoStack(op.src.A, op.src.B);
		TwoStack dest = new TwoStack(op.dest.A, op.dest.B);
		locs.get(robotLocation).twoStack = dest;
		long asmTime = ASSEMBLE_ESTIMATE(src, dest);
		GameState newGameState = new GameState(robotLocation, "", locs,
   			 	 			     map, timeRemaining - asmTime, this, op, 0);
		if (newGameState.computeScore() < score) {
			newGameState = new GameState(robotLocation, "", locs,
			 	       	   map, timeRemaining - asmTime, this, op, movesSinceLastScore + 1);
		}
		return newGameState;
	}
	
	public int computeScore() {
		return this.score;
	}
	
	private void cacheScore() {
		int score = 0;
		int zones = 0x0;
		int stackHeight = 3;
		int stacksThatScore = 0;
		for (LocationState location : locationStates) {
			// Ally stacks
			if (validAllyStack(location)) {
				int a = stackHeight;
				score += a * a;
				zones |= 0x1;
				stacksThatScore++;
			}
			// Opponent stacks (in homebase)
			if (validOpponentStack(location)) {
				int a = stackHeight;
				score += a * a;
				zones |= 0x2;
				stacksThatScore++;
			}
			// Platform stacks
			if (validPlatformStack(location)) {
				int a = stackHeight;
				score += a * a;
				// Mevior taunt bonus
				score += 2;
				zones |= 0x4;
				stacksThatScore++;
			}
		}
		// Reverse engineer bonus
		if ((zones & 0x02) == 0x02) {
			score += 10;
		}
		// Zone mulitplier
		int multiplier = 0;
		for (int i = zones; i > 0; i = i >> 1) {
			multiplier += (i & 0x1);
		}
		this.numZones = multiplier;
		this.score = multiplier * score;
		this.stacksThatScore = stacksThatScore;
	}
	
	public int getNumZones() {
		return this.numZones;
	}
	
	public boolean validAllyStack(LocationState ls) {
		// TODO: Select actual stack string
		String A = "R";
		return (ls.type == LocationType.STACK && ls.twoStack.A.equals(A + A + A)) ||
			   (ls.type == LocationType.STACK && ls.twoStack.B.equals(A + A + A));
	}
	
	public boolean validOpponentStack(LocationState ls) {
		// TODO: Select actual stack string
		String B = "G";
		return (ls.type == LocationType.HOMEBASE && ls.twoStack.A.equals(B + B + B)) ||
			   (ls.type == LocationType.HOMEBASE && ls.twoStack.B.equals(B + B + B));
	}
	
	public boolean validPlatformStack(LocationState ls) {
		// TODO: Select actual stack string
		String T = "R";
		if (ls.twoStack.B.length() >= 2) {
			if (ls.type == LocationType.PLATFORM && (ls.twoStack.B.charAt(ls.twoStack.B.length()-1) + "").equals(T)) {
				return true;
			}
		}
		return false;
	}
	
	// TODO: Make these estimates better?
	public long MOVE_ESTIMATE(Pose src, Pose dest) {
		long time = PathPlanning.getInstance().estimateTravelTime(src, dest);
		return time;
	}
	
	public long GRAB_ESTIMATE() {
		return 5000;
	}
	
	public long DEPLOY_ESTIMATE() {
		return 5000;
	}
	
	public long ASSEMBLE_ESTIMATE(TwoStack src, TwoStack dest) {
		int time = Assembler.estimateAssemblyTime(src, dest);
		return time;
	}
	
	public List<GameOperation> getAllowedOps() {
		List<GameOperation> ops = new ArrayList<GameOperation>();
		LocationState robLoc = locationStates.get(robotLocation);
		
		// At stacks or homebases
		if (robLoc.type == LocationType.STACK || robLoc.type == LocationType.HOMEBASE) {
			if (heldStack.length() == 0) {
				// If nothing is being held, you can grip
				if (robLoc.twoStack.A.length() > 0) {
					ops.add(new GrabPortOp(1));
				}
				if (robLoc.twoStack.B.length() > 0) {
					ops.add(new GrabPortOp(2));
				}
			} else {
				// If you are already gripping cubes, drop them in an empty port
				if (robLoc.twoStack.A.length() == 0) {
					ops.add(new DeployPortOp(1));
				}
				if (robLoc.twoStack.B.length() == 0) {
					ops.add(new DeployPortOp(2));
				}
			}
			// only allow assembly if the last step was NOT an assembly step
			if (this.parent == null || !(this.op instanceof AssembleOp)) {
				// ASSMEBLY OPTIONS if BOTH ports are loaded
				if (robLoc.twoStack.A.length() > 0 && robLoc.twoStack.B.length() > 0) {
					TwoStack[] crossOptions = robLoc.twoStack.getCrossOptions();
					for (int i = 0; i < crossOptions.length; i++) {
						ops.add(new AssembleOp(robLoc.twoStack, crossOptions[i]));
					}
				} else {
					// ASSMEBLY OPTIONS if ONE port is loaded
					if (robLoc.twoStack.A.length() > 0 || robLoc.twoStack.B.length() > 0) {
						TwoStack[] reorderOptions = robLoc.twoStack.getReorderOptions();
						for (int i = 0; i < reorderOptions.length; i++) {
							ops.add(new AssembleOp(robLoc.twoStack, reorderOptions[i]));
						}
					}
				}
			}
		} else if (robLoc.type == LocationType.PLATFORM) {
			Point dest = new Point(
				robLoc.pose.x - Math.cos(robLoc.pose.theta) * Config.HUB_DISTANCE,
				robLoc.pose.y - Math.sin(robLoc.pose.theta) * Config.HUB_DISTANCE
			);
			ops.add(new DeployPlatformOp(robotLocation, dest));
		}
		
		// All navigation options except self
		if (this.parent==null || this.parent !=null && !(this.op instanceof MoveToLocationOp)) {
			for (int i = 1; i < locationStates.size(); i++) {
				if (i != robotLocation && locationStates.get(i).visited == 0) {
					ops.add(new MoveToLocationOp(i));
				}
			}
		}
		
		return ops;
	}
	
	public String toSettingsString() {
		String settings = "";
		settings += "N," + locationStates.size() + "\n";
		for (int i = 0; i < locationStates.size(); i++) {
			LocationState loc = locationStates.get(i);
			settings += "LOC," + i + "," + (float)loc.pose.x + "," + (float)loc.pose.y + "," + (float)loc.pose.theta;
			if (i < locationStates.size() - 1) {
				settings += "\n";
			}
		}
		return settings;
	}
}
