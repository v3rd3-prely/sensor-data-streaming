package ro.tuiasi.ac.server;

import ro.tuiasi.ac.common.*;

/**
 * Processes sensor data to detect obstacles and visual targets, returning
 * appropriate robot commands.
 * 
 * @author Your Name
 */
public class ProcessingSensor {

	/**
	 * Minimum distance threshold in cm to consider an obstacle (values below this
	 * trigger avoidance)
	 */
	private static final double lidarThresh = 5;

	/**
	 * Minimum red channel value to detect a red target pixel (requires red greater
	 * than 200, green less than 50, blue less than 50)
	 */
	private static final int redThresh = 200;

	/**
	 * Private constructor to prevent instantiation. This is a utility class with
	 * only static methods.
	 */
	private ProcessingSensor() {
		// Utility class - no instantiation needed
	}

	/**
	 * Analyzes sensor data and returns a command for robot navigation.
	 * 
	 * <p>
	 * Priority order:
	 * <ol>
	 * <li>Left obstacle detection -> move RIGHT</li>
	 * <li>Right obstacle detection -> move LEFT</li>
	 * <li>Red target detection -> rotate or move toward target</li>
	 * <li>No obstacles or targets -> STOP</li>
	 * </ol>
	 * 
	 * @param data The sensor dataset containing LiDAR and camera frames
	 * @return Command to execute (MoveCommand, RotateCommand, or StopCommand)
	 */
	public static Command processSensorDataSet(SensorDataSet data) {
		double[][] leftLidarVals = data.leftLidarFrame().distancesCm();
		double[][] rightLidarVals = data.rightLidarFrame().distancesCm();

		boolean obstacleLeft = false;
		boolean obstacleRight = false;

		// Check left LiDAR for obstacles
		for (int i = 0; i < leftLidarVals.length; i++) {
			for (int j = 0; j < leftLidarVals[i].length; j++) {
				if (leftLidarVals[i][j] < lidarThresh) {
					obstacleLeft = true;
				}
			}
		}

		// Check right LiDAR for obstacles
		for (int i = 0; i < rightLidarVals.length; i++) {
			for (int j = 0; j < rightLidarVals[i].length; j++) {
				if (rightLidarVals[i][j] < lidarThresh) {
					obstacleRight = true;
				}
			}
		}

		// Obstacle avoidance logic
		if (obstacleLeft) {
			System.out.println("Obstacle on the left side detected.");
			return new MoveCommand(MoveDirection.RIGHT, 10);
		}

		if (obstacleRight) {
			System.out.println("Obstacle on the right side detected.");
			return new MoveCommand(MoveDirection.LEFT, 10);
		}

		// Camera processing for red target detection
		int[][] red = data.cameraFrame().red();
		int[][] green = data.cameraFrame().green();
		int[][] blue = data.cameraFrame().blue();

		int width = data.cameraFrame().width();
		int height = data.cameraFrame().height();

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (red[j][i] > redThresh && green[j][i] < 50 && blue[j][i] < 50) {
					int targetX = i;
					int targetY = j;
					if (targetX + 10 < width / 2) {
						System.out.println("Rotation left towards the target.");
						return new RotateCommand(RotateDirection.LEFT, 10);
					} else if (targetX - 10 > width / 2) {
						System.out.println("Rotation right towards the target.");
						return new RotateCommand(RotateDirection.RIGHT, 10);
					} else if (targetY - 10 < height) {
						System.out.println("Moving towards the target.");
						return new MoveCommand(MoveDirection.FRONT, 10);
					} else {
						System.out.println("Target reached.");
						return new StopCommand();
					}
				}
			}
		}

		return new StopCommand();
	}
}