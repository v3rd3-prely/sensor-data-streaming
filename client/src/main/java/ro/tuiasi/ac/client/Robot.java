package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.*;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;

/**
 * Represents a robot that collects sensor data and executes commands.
 * 
 * @author Your Name
 */
public class Robot {

	/** The sensor set attached to this robot */
	private final SensorSet sensorSet;

	/** Flag indicating whether the robot is actively sending data */
	private boolean isSendingData;

	/**
	 * Creates a new robot with the specified sensor set.
	 * 
	 * @param sensorSet The sensor configuration for this robot
	 */
	public Robot(SensorSet sensorSet) {
		this.sensorSet = sensorSet;
		this.isSendingData = false;
	}

	/**
	 * Collects current data from all sensors.
	 * 
	 * @return SensorDataSet containing readings from all sensors
	 */
	public SensorDataSet collectData() {
		return sensorSet.collectData();
	}

	/**
	 * Checks if the robot is currently sending data.
	 * 
	 * @return true if sending data, false otherwise
	 */
	public boolean isSending() {
		return this.isSendingData;
	}

	/**
	 * Executes a command on the robot.
	 * 
	 * <p>
	 * Supported command types:
	 * <ul>
	 * <li>{@link StartCommand} - Starts data transmission</li>
	 * <li>{@link StopCommand} - Stops data transmission</li>
	 * <li>{@link MoveCommand} - Moves the camera target position</li>
	 * <li>{@link RotateCommand} - Rotates the camera target position</li>
	 * </ul>
	 * 
	 * @param cmd The command to execute
	 */
	public void executeCommand(Command cmd) {
		if (cmd instanceof StartCommand) {
			this.isSendingData = true;
			System.out.println("Started sending data.");
		} else if (cmd instanceof StopCommand) {
			this.isSendingData = false;
			System.out.println("Stopped sending data.");
		} else if (cmd instanceof MoveCommand mov) {
			MoveDirection dir = mov.getDirection();
			float dist = mov.getDistance();
			int offsetX = 0, offsetY = 0;
			switch (dir) {
			case LEFT:
				offsetX += dist;
				break;
			case RIGHT:
				offsetX -= dist;
				break;
			case FRONT:
				offsetY += dist;
				break;
			case BACK:
				offsetY -= dist;
				break;
			default:
				System.out.println("Move direction unknown.");
			}
			int newTargetX = sensorSet.cameraSensor.getTargetX() + offsetX;
			int newTargetY = sensorSet.cameraSensor.getTargetY() + offsetY;

			if (newTargetX < 0) {
				newTargetX = 0;
			}
			if (newTargetY < 0) {
				newTargetY = 0;
			}
			if (newTargetX > 400) {
				newTargetX = 400;
			}
			if (newTargetY < 400) {
				newTargetY = 400;
			}

			sensorSet.cameraSensor.setTargetPosition(newTargetX, newTargetY);
			System.out.println("Move command executed.");
		} else if (cmd instanceof RotateCommand rot) {
			RotateDirection dir = rot.getOrientation();
			float degrees = rot.getGrade();
			switch (dir) {
			case LEFT:
				break;
			case RIGHT:
				degrees = -degrees;
				break;
			default:
				System.out.println("Rotation direction unknown.");
			}
			int newTargetX = (int) (sensorSet.cameraSensor.getTargetX() + degrees);
			int newTargetY = sensorSet.cameraSensor.getTargetY();

			if (newTargetX < 0) {
				newTargetX = 0;
			}

			if (newTargetX > 400) {
				newTargetX = 400;
			}
			sensorSet.cameraSensor.setTargetPosition(newTargetX, newTargetY);
			sensorSet.gyroscopeSensor.update(degrees, degrees);
			System.out.println("Rotate command executed.");
		} else {
			System.out.println("Command unknown");
		}
	}
}