package mission.gameplan.operations;

public class GrabPortOp extends GameOperation {
	public final int port;
	public GrabPortOp(int port) {
		this.port = port;
	}
	public String toPlanString() {
		return "GRAB," + port;
	}
}
