package mission.gameplan.operations;

public class MoveToLocationOp extends GameOperation {
	public final int loc;
	public MoveToLocationOp(int loc) {
		this.loc = loc;
	}
	public String toPlanString() {
		return "MOV," + loc;
	}
}
