package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoveCommand implements Command {
	private MoveDirection direction;
	private float distanceCm;

	// DEFAULT CONSTRUCTOR - REQUIRED for Jackson
	public MoveCommand() {
	}

	// Parameterized constructor for your code
	public MoveCommand(MoveDirection dir, float dist) {
		this.direction = dir;
		this.distanceCm = dist;
	}

	@Override
	public CommandType getType() {
		return CommandType.MOVE;
	}

	// GETTERS - REQUIRED for Jackson serialization
	public MoveDirection getDirection() {
		return this.direction;
	}

	// SETTERS - REQUIRED for Jackson deserialization
	public void setDirection(MoveDirection direction) {
		this.direction = direction;
	}

	public float getDistance() {
		return this.distanceCm;
	}

	public void setDistance(float distanceCm) {
		this.distanceCm = distanceCm;
	}

	@Override
	public String toString() {
		return String.format("MoveCommand{direction=%s, distance=%.1fcm}", direction, distanceCm);
	}
}