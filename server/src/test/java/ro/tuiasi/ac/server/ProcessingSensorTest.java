package ro.tuiasi.ac.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ro.tuiasi.ac.common.CameraFrame;
import ro.tuiasi.ac.common.Command;
import ro.tuiasi.ac.common.CommandType;
import ro.tuiasi.ac.common.GyroscopeFrame;
import ro.tuiasi.ac.common.LidarFrame;
import ro.tuiasi.ac.common.MoveCommand;
import ro.tuiasi.ac.common.MoveDirection;
import ro.tuiasi.ac.common.RotateCommand;
import ro.tuiasi.ac.common.RotateDirection;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.StopCommand;

/**
 * Test class for {@link ProcessingSensor}.
 * Verifies obstacle avoidance logic and visual target detection behavior
 * across various sensor input scenarios.
 *
 * @author Your Name
 */
public final class ProcessingSensorTest {

    /** Camera frame width and height in pixels. */
    private static final int CAMERA_SIZE = 400;

    /** LiDAR frame width and height in points. */
    private static final int LIDAR_SIZE = 100;

    /** Red pixel value for target detection. */
    private static final int RED_PIXEL = 255;

    /** Distance threshold for obstacle detection (cm). */
    private static final double OBSTACLE_THRESH = 4.0;

    /** Safe distance value (no obstacle). */
    private static final double SAFE_DISTANCE = 100.0;

    /** Default move/rotate distance/angle. */
    private static final float DEFAULT_STEP = 10.0f;

    /** X coordinate for left-side target. */
    private static final int TARGET_LEFT_X = 50;

    /** X coordinate for right-side target. */
    private static final int TARGET_RIGHT_X = 350;

    /** Y coordinate for target (center). */
    private static final int TARGET_CENTER_Y = 200;

    /** X coordinate for centered target. */
    private static final int TARGET_CENTER_X = 200;

    /** Blue pixel value for non-red test. */
    private static final int BLUE_PIXEL = 255;

    /** Non-red pixel value. */
    private static final int NON_RED_VALUE = 100;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private ProcessingSensorTest() {
        // Test class - no instantiation needed
    }

    /**
     * Creates a camera frame with a single red pixel
     * at the specified coordinates.
     * Used to simulate visual target detection.
     *
     * @param targetX the X coordinate of the red pixel
     * @param targetY the Y coordinate of the red pixel
     * @return CameraFrame with a single red pixel at the target location
     */
    private CameraFrame cameraFrameWithRedPixel(final int targetX,
            final int targetY) {
        int[][] red = new int[CAMERA_SIZE][CAMERA_SIZE];
        int[][] green = new int[CAMERA_SIZE][CAMERA_SIZE];
        int[][] blue = new int[CAMERA_SIZE][CAMERA_SIZE];

        red[targetY][targetX] = RED_PIXEL;
        green[targetY][targetX] = 0;
        blue[targetY][targetX] = 0;

        return new CameraFrame(CAMERA_SIZE, CAMERA_SIZE, red, green, blue);
    }

    /**
     * Creates an empty camera frame with all black pixels.
     * Used to simulate no visual target detected.
     *
     * @return CameraFrame with all black pixels
     */
    private CameraFrame emptyCameraFrame() {
        return new CameraFrame(
                CAMERA_SIZE,
                CAMERA_SIZE,
                new int[CAMERA_SIZE][CAMERA_SIZE],
                new int[CAMERA_SIZE][CAMERA_SIZE],
                new int[CAMERA_SIZE][CAMERA_SIZE]
        );
    }

    /**
     * Creates a LiDAR frame where all distance measurements are equal to the
     * specified value. Used to simulate uniform obstacle distance.
     *
     * @param distance the uniform distance value for all cells (in cm)
     * @return LidarFrame with all distances set to the given value
     */
    private LidarFrame lidarFrameWithDistance(final double distance) {
        double[][] distances = new double[LIDAR_SIZE][LIDAR_SIZE];

        for (int y = 0; y < LIDAR_SIZE; y++) {
            for (int x = 0; x < LIDAR_SIZE; x++) {
                distances[y][x] = distance;
            }
        }
        return new LidarFrame(LIDAR_SIZE, LIDAR_SIZE, distances);
    }

    /**
     * Creates a complete SensorDataSet with the given camera and LiDAR frames.
     * Gyroscope frame is initialized with zero values.
     *
     * @param cameraFrame the camera frame to include
     * @param leftLidar the left LiDAR frame
     * @param rightLidar the right LiDAR frame
     * @return SensorDataSet containing all provided frames
     */
    private SensorDataSet sensorDataSet(final CameraFrame cameraFrame,
            final LidarFrame leftLidar, final LidarFrame rightLidar) {
        return new SensorDataSet(
                cameraFrame,
                leftLidar,
                rightLidar,
                new GyroscopeFrame(0.0, 0.0)
        );
    }

    /**
     * Tests that an obstacle on the left causes the robot to move right.
     * Verifies that when left LiDAR detects an obstacle below threshold,
     * a MoveCommand with RIGHT direction is returned.
     */
    @Test
    void shouldMoveRightWhenObstacleIsDetectedOnLeft() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(OBSTACLE_THRESH),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.RIGHT, moveCommand.getDirection());
        assertEquals(DEFAULT_STEP, moveCommand.getDistance());
    }

    /**
     * Tests that an obstacle on the right causes the robot to move left.
     * Verifies that when right LiDAR detects an obstacle below threshold,
     * a MoveCommand with LEFT direction is returned.
     */
    @Test
    void shouldMoveLeftWhenObstacleIsDetectedOnRight() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(OBSTACLE_THRESH)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.LEFT, moveCommand.getDirection());
        assertEquals(DEFAULT_STEP, moveCommand.getDistance());
    }

    /**
     * Tests that a target on the left side causes left rotation.
     * Verifies that when a red target is detected on the left half,
     * a RotateCommand with LEFT orientation is returned.
     */
    @Test
    void shouldRotateLeftWhenTargetIsOnLeftSide() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(TARGET_LEFT_X, TARGET_CENTER_Y),
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof RotateCommand);

        RotateCommand rotateCommand = (RotateCommand) command;

        assertEquals(RotateDirection.LEFT, rotateCommand.getOrientation());
        assertEquals(DEFAULT_STEP, rotateCommand.getGrade());
    }

    /**
     * Tests that a target on the right side causes right rotation.
     * Verifies that when a red target is detected on the right half,
     * a RotateCommand with RIGHT orientation is returned.
     */
    @Test
    void shouldRotateRightWhenTargetIsOnRightSide() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(TARGET_RIGHT_X, TARGET_CENTER_Y),
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof RotateCommand);

        RotateCommand rotateCommand = (RotateCommand) command;

        assertEquals(RotateDirection.RIGHT, rotateCommand.getOrientation());
        assertEquals(DEFAULT_STEP, rotateCommand.getGrade());
    }

    /**
     * Tests that a centered target causes forward movement.
     * Verifies that when a red target is centered in the image,
     * a MoveCommand with FRONT direction is returned.
     */
    @Test
    void shouldMoveForwardWhenTargetIsCentered() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(TARGET_CENTER_X, TARGET_CENTER_Y),
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.FRONT, moveCommand.getDirection());
        assertEquals(DEFAULT_STEP, moveCommand.getDistance());
    }

    /**
     * Tests that the robot stops when no obstacles or targets are detected.
     * Verifies that with all clear sensor readings, a StopCommand is returned.
     */
    @Test
    void shouldStopWhenNoObstacleAndNoTarget() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof StopCommand);
        assertEquals(CommandType.STOP, command.getType());
    }

    /**
     * Tests that non-red pixels are ignored by the target detection logic.
     * Verifies that a blue pixel does not trigger target response and results
     * in a StopCommand.
     */
    @Test
    void shouldIgnoreNonRedPixels() {
        int[][] red = new int[CAMERA_SIZE][CAMERA_SIZE];
        int[][] green = new int[CAMERA_SIZE][CAMERA_SIZE];
        int[][] blue = new int[CAMERA_SIZE][CAMERA_SIZE];

        red[TARGET_CENTER_Y][TARGET_CENTER_X] = NON_RED_VALUE;
        green[TARGET_CENTER_Y][TARGET_CENTER_X] = NON_RED_VALUE;
        blue[TARGET_CENTER_Y][TARGET_CENTER_X] = BLUE_PIXEL;

        CameraFrame cameraFrame = new CameraFrame(
                CAMERA_SIZE, CAMERA_SIZE, red, green, blue);

        SensorDataSet dataSet = sensorDataSet(
                cameraFrame,
                lidarFrameWithDistance(SAFE_DISTANCE),
                lidarFrameWithDistance(SAFE_DISTANCE)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof StopCommand);
    }
}
