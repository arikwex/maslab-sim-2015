package mission.gameplan.operations;

public class DeployPortOp extends GameOperation {
	public final int port;
	public DeployPortOp(int port) {
		this.port = port;
	}
	public String toPlanString() {
		return "DEPLOY," + port;
	}
}
