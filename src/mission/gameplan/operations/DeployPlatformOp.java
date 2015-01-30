package mission.gameplan.operations;

import map.geom.Point;

public class DeployPlatformOp extends GameOperation {
	public final int loc;
	public final Point dest;
	public DeployPlatformOp(int loc, Point dest) {
		this.loc = loc;
		this.dest = dest;
	}
	public String toPlanString() {
		return "PLATFORM," + loc;
	}
}
