package ro.tuiasi.ac.client;

import org.junit.jupiter.api.Test;
import ro.tuiasi.ac.common.*;

import static org.junit.jupiter.api.Assertions.*;

public class RobotTest {

    private Robot createRobot() {

        CameraSensor cameraSensor = new CameraSensor("camera", 100, 100);

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

    @Test
    void robotShouldInitiallyNotSendData() {

        Robot robot = createRobot();

        assertFalse(robot.isSending());
    }

    @Test
    void startCommandShouldEnableSending() {

        Robot robot = createRobot();

        robot.executeCommand(new StartCommand());

        assertTrue(robot.isSending());
    }

    @Test
    void stopCommandShouldDisableSending() {

        Robot robot = createRobot();

        robot.executeCommand(new StartCommand());
        robot.executeCommand(new StopCommand());

        assertFalse(robot.isSending());
    }

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

    @Test
    void moveCommandShouldChangeCameraPosition() {

        Robot robot = createRobot();

        SensorSet sensorSet = new SensorSet(
                new CameraSensor("camera", 100, 100),
                new LidarSensor("left"),
                new LidarSensor("right"),
                new GyroscopeSensor("gyro")
        );

        Robot testRobot = new Robot(sensorSet);

        int initialX = sensorSet.cameraSensor.getTargetX();

        testRobot.executeCommand(
                new MoveCommand(MoveDirection.LEFT, 20)
        );

        int updatedX = sensorSet.cameraSensor.getTargetX();

        assertNotEquals(initialX, updatedX);
    }

    @Test
    void rotateCommandShouldUpdateGyroscope() {

        CameraSensor cameraSensor = new CameraSensor("camera", 100, 100);

        GyroscopeSensor gyro = new GyroscopeSensor("gyro");

        SensorSet sensorSet = new SensorSet(
                cameraSensor,
                new LidarSensor("left"),
                new LidarSensor("right"),
                gyro
        );

        Robot robot = new Robot(sensorSet);

        robot.executeCommand(
                new RotateCommand(RotateDirection.LEFT, 45)
        );

        GyroscopeFrame frame = gyro.readData();

        assertEquals(45.0, frame.headingDegrees());
        assertEquals(45.0, frame.angularVelocityDegreesPerSecond());
    }
}