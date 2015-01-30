package mission.gameplan;

import hardware.enums.ElevatorState;
import hardware.enums.GripperState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import state_machine.State;
import state_machine.game.AimState;
import state_machine.game.ApplyElevatorState;
import state_machine.game.ApplyGripperState;
import state_machine.game.BackTravelState;
import state_machine.game.DriveToStackState;
import state_machine.game.PlannerState;
import state_machine.game.TravelState;
import map.Map;
import map.Pose;
import map.geom.Point;

public class PlanLoader {
	private static Pose hubPose = null;
	private static Point aimPort = null;
	private static ElevatorState lastElevation = null;
	
	public static Queue<State> load(PlannerState ps, File planFile) {
		System.out.println("Loading plan");
		List<Pose> locations = new ArrayList<Pose>();
		Queue<State> ops = new LinkedList<State>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(planFile));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				process(line, ps, locations, ops);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map.getInstance().setLocations(locations);
		System.out.println("Plan queue has " + ops.size() + " operations.");
		return ops;
	}
	
	public static void process(String line, PlannerState ps, List<Pose> loc, Queue<State> ops) {
		String[] params = line.split(",");
		String type = params[0];
		
		if (type.equals("LOC")) {
			// LOCATION OBJECT
			loc.add(new Pose(Double.parseDouble(params[2]),
							 Double.parseDouble(params[3]),
							 Double.parseDouble(params[4])));
		} else if (type.equals("MOV")) {
			// MOVE
			int location = Integer.parseInt(params[1]);
			Pose pose = loc.get(location);
			System.out.println("MOVE TO LOC [" + location + "]: " + pose);
			ops.add(new TravelState(ps, pose));
			hubPose = new Pose(pose.x, pose.y, pose.theta);
		} else if (type.equals("GRAB")) {
			// GRAB FULL STACK
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			int index = Integer.parseInt(params[1]) - 1;
			System.out.println("GRAB FULL STACK (port = " + (index+1) + ")");
			
			ops.add(new AimState(ps, ports[index]));
			ops.add(new DriveToStackState(ps, ports[index]));
	    	
			ops.add(new ApplyElevatorState(ps, ElevatorState.BOTTOM));
			ops.add(new ApplyGripperState(ps, GripperState.CLOSE));
			
			ops.add(new ApplyElevatorState(ps, ElevatorState.TRANSIT));
			ops.add(new BackTravelState(ps, start));
		} else if (type.equals("DEPLOY")) {
			// DEPLOY FULL STACK
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			int index = Integer.parseInt(params[1]) - 1;
			System.out.println("DEPLOY FULL STACK (port = " + (index+1) + ")");
			
			ops.add(new ApplyElevatorState(ps, ElevatorState.BOTTOM));
	    	
			ops.add(new AimState(ps, ports[index]));
			ops.add(new DriveToStackState(ps, ports[index]));
	    	
			ops.add(new ApplyGripperState(ps, GripperState.OPEN));
			ops.add(new BackTravelState(ps, start));
		} else if (type.equals("PLATFORM")) {
			// DEPLOY FULL STACK to PLATFORM
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			int location = Integer.parseInt(params[1]);
			System.out.println("DEPLOY TO PLATFORM [" + location + "]: " + start);
			
			ops.add(new ApplyElevatorState(ps, ElevatorState.TOP));
			ops.add(new AimState(ps, ports[1]));
			ops.add(new DriveToStackState(ps, ports[1]));
			ops.add(new ApplyElevatorState(ps, ElevatorState.MIDDLE));
			ops.add(new ApplyGripperState(ps, GripperState.OPEN));
			ops.add(new ApplyElevatorState(ps, ElevatorState.TRANSIT));
			ops.add(new BackTravelState(ps, start));
		} else if (type.equals("_BOT")) {
			// SET ELEVATOR TO BOT
			lastElevation = ElevatorState.BOTTOM;
		} else if (type.equals("_MID")) {
			// SET ELEVATOR TO MID
			lastElevation = ElevatorState.MIDDLE;
		} else if (type.equals("_TOP")) {
			// SET ELEVATOR TO TOP
			lastElevation = ElevatorState.TOP;
		} else if (type.equals("_T1")) {
			// SET PORT TO 1
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			ops.add(new AimState(ps, ports[0]));
			aimPort = ports[0];
		} else if (type.equals("_T2")) {
			// SET PORT TO 2
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			ops.add(new AimState(ps, ports[1]));
			aimPort = ports[1];
		} else if (type.equals("_T3")) {
			// SET PORT TO 3
			Point start = new Point(hubPose.x, hubPose.y);
			Point[] ports = PlannerState.getPorts(start, hubPose.theta);
			ops.add(new AimState(ps, ports[2]));
			aimPort = ports[2];
		} else if (type.equals("_GRAB")) {
			// GRAB FOR ASSMEBLY
			Point start = new Point(hubPose.x, hubPose.y);
			Point aim = new Point(aimPort.x, aimPort.y);
			ops.add(new ApplyGripperState(ps, GripperState.OPEN));
			ops.add(new DriveToStackState(ps, aim));
			ops.add(new ApplyElevatorState(ps, lastElevation));
			ops.add(new ApplyGripperState(ps, GripperState.CLOSE));
			ops.add(new ApplyElevatorState(ps, ElevatorState.TRANSIT));
			ops.add(new BackTravelState(ps, start));
		} else if (type.equals("_DEPLOY")) {
			// DEPLOY FOR ASSMEBLY
			Point start = new Point(hubPose.x, hubPose.y);
			Point aim = new Point(aimPort.x, aimPort.y);
			ops.add(new DriveToStackState(ps, aim));
			ops.add(new ApplyElevatorState(ps, lastElevation));
			ops.add(new ApplyGripperState(ps, GripperState.OPEN));
			ops.add(new ApplyElevatorState(ps, ElevatorState.TRANSIT));
			ops.add(new BackTravelState(ps, start));
		}
	}
}
