package mission.gameplan;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import state_machine.State;
import state_machine.game.PlannerState;
import state_machine.game.TravelState;
import map.Map;
import map.Pose;
import map.geom.Point;

public class PlanLoader {
	public static Queue<State> load(PlannerState ps, File planFile) {
		System.out.println("Loading plan");
		List<Point> locations = new ArrayList<Point>();
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
		System.out.println("Plan queue has " + ops.size() + " operations.");
		return ops;
	}
	
	public static void process(String line, PlannerState ps, List<Point> loc, Queue<State> ops) {
		String[] params = line.split(",");
		String type = params[0];
		
		if (type.equals("LOC")) {
			loc.add(new Point(Double.parseDouble(params[1]),
							 Double.parseDouble(params[2])));
		} else if (type.equals("MOV")) {
			int location = Integer.parseInt(params[1]);
			Point pt = loc.get(location);
			ops.add(new TravelState(ps, pt));
		}
	}
}
