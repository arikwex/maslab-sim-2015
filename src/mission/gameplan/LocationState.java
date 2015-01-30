package mission.gameplan;

import map.Pose;
import map.geom.Point;
import mission.TwoStack;

public class LocationState {
	public TwoStack twoStack;
	public final LocationType type;
	public final Pose pose;
	
	public LocationState(TwoStack twoStack, LocationType type, Pose pose) {
		this.twoStack = twoStack;
		this.type = type;
		this.pose = pose;
	}
	
	public String toString() {
		return twoStack.toString();
	}
}
