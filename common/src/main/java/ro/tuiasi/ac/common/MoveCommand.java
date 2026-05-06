package ro.tuiasi.ac.common;

public class MoveCommand implements Command {
	
	private MoveDirection direction;
	private float distanceCm;
	
	public MoveCommand(MoveDirection dir, float dist) {
		this.direction = dir;
		this.distanceCm = dist;
	}
	
	@Override
	public CommandType getType() {
		// TODO Auto-generated method stub
		return CommandType.MOVE;
	}
	
	public MoveDirection getDirection() {
		return this.direction;
	}
	
	public float getDistance() {
		return this.distanceCm;
	}
	
}
