package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Command to rotate the robot by a specified angle in a given direction.
 * 
 * @author Your Name
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RotateCommand implements Command {

	/** Direction to rotate (LEFT or RIGHT) */
	private RotateDirection orientation;

	/** Rotation angle in degrees */
	private float grade;

	/**
	 * Default constructor required for Jackson deserialization.
	 */
	public RotateCommand() {
	}

	/**
	 * Creates a new rotate command.
	 * 
	 * @param orient Direction to rotate (LEFT or RIGHT)
	 * @param g      Rotation angle in degrees
	 */
	public RotateCommand(RotateDirection orient, float g) {
		this.orientation = orient;
		this.grade = g;
	}

	@Override
	public CommandType getType() {
		return CommandType.ROTATE;
	}

	/**
	 * Gets the rotation direction.
	 * 
	 * @return The rotation direction (LEFT or RIGHT)
	 */
	public RotateDirection getOrientation() {
		return this.orientation;
	}

	/**
	 * Gets the rotation angle.
	 * 
	 * @return Rotation angle in degrees
	 */
	public float getGrade() {
		return this.grade;
	}

	/**
	 * Sets the rotation direction.
	 * 
	 * @param orientation The rotation direction to set
	 */
	public void setOrientation(RotateDirection orientation) {
		this.orientation = orientation;
	}

	/**
	 * Sets the rotation angle.
	 * 
	 * @param grade Rotation angle in degrees to set
	 */
	public void setGrade(float grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return String.format("RotateCommand{orientation=%s, grade=%.1f°}", orientation, grade);
	}
}