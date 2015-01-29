package mission.gameplan;

import map.Pose;
import map.geom.Point;

public class LocationState {
	public final String stack;
	public final LocationType type;
	public final Pose pose;
	
	public LocationState(String stack, LocationType type, Pose pose) {
		this.stack = stack;
		this.type = type;
		this.pose = pose;
	}
}
