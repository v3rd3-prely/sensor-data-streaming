package ro.tuiasi.ac.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.tuiasi.ac.common.Command;
import ro.tuiasi.ac.common.MoveCommand;
import ro.tuiasi.ac.common.MoveDirection;
import ro.tuiasi.ac.common.RotateCommand;
import ro.tuiasi.ac.common.RotateDirection;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.StopCommand;

/**
 * Processes sensor data to detect obstacles and visual targets, returning
 * appropriate robot commands.
 *
 * @author Your Name
 */
public final class ProcessingSensor {

    /** Minimum distance threshold in cm to consider an obstacle. */
    private static final double LIDAR_THRESH = 5;

    /** Minimum red channel value to detect a red target pixel. */
    private static final int RED_THRESH = 200;

    /** Green channel upper threshold for red detection. */
    private static final int GREEN_BLUE_THRESH = 50;

    /** Movement distance for avoidance commands in cm. */
    private static final int AVOID_DISTANCE_CM = 10;

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(
            ProcessingSensor.class);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ProcessingSensor() {
        // Utility class - no instantiation needed
    }

    /**
     * Analyzes sensor data and returns a command for robot navigation.
     *
     * <p>Priority order:
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
    public static Command processSensorDataSet(final SensorDataSet data) {
        double[][] leftLidarVals = data.leftLidarFrame().distancesCm();
        double[][] rightLidarVals = data.rightLidarFrame().distancesCm();

        boolean obstacleLeft = false;
        boolean obstacleRight = false;

        // Check left LiDAR for obstacles
        for (double[] ds : leftLidarVals) {
            for (double val : ds) {
                if (val < LIDAR_THRESH) {
                    obstacleLeft = true;
                }
            }
        }

        // Check right LiDAR for obstacles
        for (double[] ds : rightLidarVals) {
            for (double val : ds) {
                if (val < LIDAR_THRESH) {
                    obstacleRight = true;
                }
            }
        }

        // Obstacle avoidance logic
        if (obstacleLeft) {
            LOG.info("Obstacle on the left side detected.");
            return new MoveCommand(MoveDirection.RIGHT, AVOID_DISTANCE_CM);
        }

        if (obstacleRight) {
            LOG.info("Obstacle on the right side detected.");
            return new MoveCommand(MoveDirection.LEFT, AVOID_DISTANCE_CM);
        }

        // Camera processing for red target detection
        int[][] red = data.cameraFrame().red();
        int[][] green = data.cameraFrame().green();
        int[][] blue = data.cameraFrame().blue();

        int width = data.cameraFrame().width();
        int height = data.cameraFrame().height();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (red[j][i] > RED_THRESH
                        && green[j][i] < GREEN_BLUE_THRESH
                        && blue[j][i] < GREEN_BLUE_THRESH) {

                    int targetX = i;
                    int targetY = j;
                    int halfWidth = width / 2;

                    if (targetX + AVOID_DISTANCE_CM < halfWidth) {
                        LOG.info("Rotation left towards the target.");
                        return new RotateCommand(
                                RotateDirection.LEFT, AVOID_DISTANCE_CM);
                    } else if (targetX - AVOID_DISTANCE_CM > halfWidth) {
                        LOG.info("Rotation right towards the target.");
                        return new RotateCommand(
                                RotateDirection.RIGHT, AVOID_DISTANCE_CM);
                    } else if (targetY - AVOID_DISTANCE_CM < height) {
                        LOG.info("Moving towards the target.");
                        return new MoveCommand(
                                MoveDirection.FRONT, AVOID_DISTANCE_CM);
                    } else {
                        LOG.info("Target reached.");
                        return new StopCommand();
                    }
                }
            }
        }

        return new StopCommand();
    }
}
