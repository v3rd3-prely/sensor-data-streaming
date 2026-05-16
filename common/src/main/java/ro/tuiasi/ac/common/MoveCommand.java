package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Command to move the robot a specified distance in a given direction.
 * 
 * @author Your Name
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveCommand implements Command {

	/** Direction to move (FORWARD, BACKWARD, LEFT, RIGHT) */
	private MoveDirection direction;

	/** Distance to move in centimeters */
	private float distanceCm;

	/**
	 * Default constructor required for Jackson deserialization.
	 */
	public MoveCommand() {
	}

	/**
	 * Creates a new move command.
	 * 
	 * @param dir  Direction to move
	 * @param dist Distance in centimeters
	 */
	public MoveCommand(MoveDirection dir, float dist) {
		this.direction = dir;
		this.distanceCm = dist;
	}

	@Override
	public CommandType getType() {
		return CommandType.MOVE;
	}

	/**
	 * Gets the movement direction.
	 * 
	 * @return The movement direction
	 */
	public MoveDirection getDirection() {
		return this.direction;
	}

	/**
	 * Sets the movement direction.
	 * 
	 * @param direction The movement direction to set
	 */
	public void setDirection(MoveDirection direction) {
		this.direction = direction;
	}

	/**
	 * Gets the distance to move.
	 * 
	 * @return Distance in centimeters
	 */
	public float getDistance() {
		return this.distanceCm;
	}

	/**
	 * Sets the distance to move.
	 * 
	 * @param distanceCm Distance in centimeters to set
	 */
	public void setDistance(float distanceCm) {
		this.distanceCm = distanceCm;
	}

	@Override
	public String toString() {
		return String.format("MoveCommand{direction=%s, distance=%.1fcm}", direction, distanceCm);
	}
}