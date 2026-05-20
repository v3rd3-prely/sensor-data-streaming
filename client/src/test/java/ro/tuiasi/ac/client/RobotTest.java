package ro.tuiasi.ac.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ro.tuiasi.ac.common.CameraSensor;
import ro.tuiasi.ac.common.GyroscopeFrame;
import ro.tuiasi.ac.common.GyroscopeSensor;
import ro.tuiasi.ac.common.LidarSensor;
import ro.tuiasi.ac.common.MoveCommand;
import ro.tuiasi.ac.common.MoveDirection;
import ro.tuiasi.ac.common.RotateCommand;
import ro.tuiasi.ac.common.RotateDirection;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;
import ro.tuiasi.ac.common.StartCommand;
import ro.tuiasi.ac.common.StopCommand;

/**
 * Test class for {@link Robot}.
 * Verifies robot initialization, command execution, data collection,
 * and state management functionality.
 *
 * @author Your Name
 */
public final class RobotTest {

    /** Default target position for camera sensor. */
    private static final int DEFAULT_TARGET_POS = 100;

    /** Move distance for testing. */
    private static final int MOVE_DISTANCE = 20;

    /** Rotation angle for testing. */
    private static final int ROTATION_ANGLE = 45;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private RobotTest() {
        // Test class - no instantiation needed
    }

    /**
     * Creates a fully configured Robot instance with all sensors.
     * Camera is initialized with target position at (100, 100).
     *
     * @return a Robot instance ready for testing
     */
    private Robot createRobot() {
        CameraSensor cameraSensor = new CameraSensor(
                "camera", DEFAULT_TARGET_POS, DEFAULT_TARGET_POS);

        LidarSensor leftLidar = new LidarSensor("left-lidar");
        LidarSensor rightLidar = new LidarSensor("right-lidar");

        GyroscopeSensor gyroscopeSensor = new GyroscopeSensor("gyro");

        SensorSet sensorSet = new SensorSet(
                cameraSensor,
                leftLidar,
                rightLidar,
                gyroscopeSensor
        );

        return new Robot(sensorSet);
    }

    /**
     * Tests that the robot initially does not send data.
     * Verifies that isSending returns false before any
     * StartCommand is executed.
     *
     * @see Robot#isSending()
     */
    @Test
    void robotShouldInitiallyNotSendData() {
        Robot robot = createRobot();

        assertFalse(robot.isSending());
    }

    /**
     * Tests that StartCommand enables data sending.
     * Verifies that after executing a StartCommand, isSending returns true.
     *
     * @see StartCommand
     * @see Robot#executeCommand(Command)
     * @see Robot#isSending()
     */
    @Test
    void startCommandShouldEnableSending() {
        Robot robot = createRobot();

        robot.executeCommand(new StartCommand());

        assertTrue(robot.isSending());
    }

    /**
     * Tests that StopCommand disables data sending.
     * Verifies that after executing StartCommand followed by StopCommand,
     * isSending returns false.
     *
     * @see StopCommand
     * @see StartCommand
     * @see Robot#executeCommand(Command)
     * @see Robot#isSending()
     */
    @Test
    void stopCommandShouldDisableSending() {
        Robot robot = createRobot();

        robot.executeCommand(new StartCommand());
        robot.executeCommand(new StopCommand());

        assertFalse(robot.isSending());
    }

    /**
     * Tests that collectData returns a complete SensorDataSet.
     * Verifies that the returned data set contains non-null frames for
     * camera, left LiDAR, right LiDAR, and gyroscope sensors.
     *
     * @see Robot#collectData()
     * @see SensorDataSet
     */
    @Test
    void collectDataShouldReturnSensorDataSet() {
        Robot robot = createRobot();

        SensorDataSet dataSet = robot.collectData();

        assertNotNull(dataSet);
        assertNotNull(dataSet.cameraFrame());
        assertNotNull(dataSet.leftLidarFrame());
        assertNotNull(dataSet.rightLidarFrame());
        assertNotNull(dataSet.gyroscopeFrame());
    }

    /**
     * Tests that MoveCommand changes the camera target position.
     * Verifies that executing a MoveCommand updates the camera's
     * target coordinates.
     *
     * @see MoveCommand
     * @see Robot#executeCommand(Command)
     * @see CameraSensor#getTargetX()
     */
    @Test
    void moveCommandShouldChangeCameraPosition() {
        SensorSet sensorSet = new SensorSet(
                new CameraSensor("camera",
                        DEFAULT_TARGET_POS, DEFAULT_TARGET_POS),
                new LidarSensor("left"),
                new LidarSensor("right"),
                new GyroscopeSensor("gyro")
        );

        Robot testRobot = new Robot(sensorSet);

        int initialX = sensorSet.getCameraSensor().getTargetX();

        testRobot.executeCommand(
                new MoveCommand(MoveDirection.LEFT, MOVE_DISTANCE)
        );

        int updatedX = sensorSet.getCameraSensor().getTargetX();

        assertNotEquals(initialX, updatedX);
    }

    /**
     * Tests that RotateCommand updates the gyroscope readings.
     * Verifies that executing a RotateCommand updates both heading and
     * angular velocity in the gyroscope sensor.
     *
     * @see RotateCommand
     * @see Robot#executeCommand(Command)
     * @see GyroscopeSensor#readData()
     * @see GyroscopeFrame#headingDegrees()
     * @see GyroscopeFrame#angularVelocityDegreesPerSecond()
     */
    @Test
    void rotateCommandShouldUpdateGyroscope() {
        CameraSensor cameraSensor = new CameraSensor(
                "camera", DEFAULT_TARGET_POS, DEFAULT_TARGET_POS);

        GyroscopeSensor gyro = new GyroscopeSensor("gyro");

        SensorSet sensorSet = new SensorSet(
                cameraSensor,
                new LidarSensor("left"),
                new LidarSensor("right"),
                gyro
        );

        Robot robot = new Robot(sensorSet);

        robot.executeCommand(
                new RotateCommand(RotateDirection.LEFT, ROTATION_ANGLE)
        );

        GyroscopeFrame frame = gyro.readData();

        assertEquals((double) ROTATION_ANGLE, frame.headingDegrees());
        assertEquals((double) ROTATION_ANGLE,
                frame.angularVelocityDegreesPerSecond());
    }
}
