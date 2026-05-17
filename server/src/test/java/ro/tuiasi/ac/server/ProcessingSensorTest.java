package ro.tuiasi.ac.server;

import org.junit.jupiter.api.Test;
import ro.tuiasi.ac.common.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessingSensorTest {

    private CameraFrame cameraFrameWithRedPixel(int targetX, int targetY) {
        int width = 400;
        int height = 400;

        int[][] red = new int[height][width];
        int[][] green = new int[height][width];
        int[][] blue = new int[height][width];

        red[targetY][targetX] = 255;
        green[targetY][targetX] = 0;
        blue[targetY][targetX] = 0;

        return new CameraFrame(width, height, red, green, blue);
    }

    private CameraFrame emptyCameraFrame() {
        return new CameraFrame(
                400,
                400,
                new int[400][400],
                new int[400][400],
                new int[400][400]
        );
    }

    private LidarFrame lidarFrameWithDistance(double distance) {
        double[][] distances = new double[100][100];

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                distances[y][x] = distance;
            }
        }

        return new LidarFrame(100, 100, distances);
    }

    private SensorDataSet sensorDataSet(
            CameraFrame cameraFrame,
            LidarFrame leftLidar,
            LidarFrame rightLidar
    ) {
        return new SensorDataSet(
                cameraFrame,
                leftLidar,
                rightLidar,
                new GyroscopeFrame(0.0, 0.0)
        );
    }

    @Test
    void shouldMoveRightWhenObstacleIsDetectedOnLeft() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(4.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.RIGHT, moveCommand.getDirection());
        assertEquals(10.0f, moveCommand.getDistance());
    }

    @Test
    void shouldMoveLeftWhenObstacleIsDetectedOnRight() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(4.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.LEFT, moveCommand.getDirection());
        assertEquals(10.0f, moveCommand.getDistance());
    }

    @Test
    void shouldRotateLeftWhenTargetIsOnLeftSide() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(50, 200),
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof RotateCommand);

        RotateCommand rotateCommand = (RotateCommand) command;

        assertEquals(RotateDirection.LEFT, rotateCommand.getOrientation());
        assertEquals(10.0f, rotateCommand.getGrade());
    }

    @Test
    void shouldRotateRightWhenTargetIsOnRightSide() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(350, 200),
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof RotateCommand);

        RotateCommand rotateCommand = (RotateCommand) command;

        assertEquals(RotateDirection.RIGHT, rotateCommand.getOrientation());
        assertEquals(10.0f, rotateCommand.getGrade());
    }

    @Test
    void shouldMoveForwardWhenTargetIsCentered() {
        SensorDataSet dataSet = sensorDataSet(
                cameraFrameWithRedPixel(200, 200),
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof MoveCommand);

        MoveCommand moveCommand = (MoveCommand) command;

        assertEquals(MoveDirection.FRONT, moveCommand.getDirection());
        assertEquals(10.0f, moveCommand.getDistance());
    }

    @Test
    void shouldStopWhenNoObstacleAndNoTarget() {
        SensorDataSet dataSet = sensorDataSet(
                emptyCameraFrame(),
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof StopCommand);
        assertEquals(CommandType.STOP, command.getType());
    }

    @Test
    void shouldIgnoreNonRedPixels() {
        int width = 400;
        int height = 400;

        int[][] red = new int[height][width];
        int[][] green = new int[height][width];
        int[][] blue = new int[height][width];

        red[200][200] = 100;
        green[200][200] = 100;
        blue[200][200] = 255;

        CameraFrame cameraFrame = new CameraFrame(width, height, red, green, blue);

        SensorDataSet dataSet = sensorDataSet(
                cameraFrame,
                lidarFrameWithDistance(100.0),
                lidarFrameWithDistance(100.0)
        );

        Command command = ProcessingSensor.processSensorDataSet(dataSet);

        assertTrue(command instanceof StopCommand);
    }
}