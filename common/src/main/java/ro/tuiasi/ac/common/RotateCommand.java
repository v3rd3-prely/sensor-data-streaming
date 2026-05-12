package ro.tuiasi.ac.common;

public class RotateCommand implements Command{
	private RotateDirection orientation;
	private float grade;
	
	public RotateCommand(RotateDirection orient, float g) {
		this.orientation = orient;
		this.grade = g;
	}
	
	@Override
	public CommandType getType() {
		// TODO Auto-generated method stub
		return CommandType.ROTATE;
	}
	
	public RotateDirection getOrientation() {
		return this.orientation;
	}
	
	public float getGrade() {
		return this.grade;
	}
	

}
