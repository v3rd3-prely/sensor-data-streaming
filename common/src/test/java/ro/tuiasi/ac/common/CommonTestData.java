package ro.tuiasi.ac.common;

public class CommonTestData {

    public static CameraFrame cameraFrame() {
        return new CameraFrame(
                400,
                400,
                new int[400][400],
                new int[400][400],
                new int[400][400]
        );
    }

    public static LidarFrame lidarFrame() {
        return new LidarFrame(
                100,
                100,
                new double[100][100]
        );
    }

    public static GyroscopeFrame gyroscopeFrame() {
        return new GyroscopeFrame(90.0, 10.0);
    }

    public static SensorDataSet sensorDataSet() {
        return new SensorDataSet(
                cameraFrame(),
                lidarFrame(),
                lidarFrame(),
                gyroscopeFrame()
        );
    }
}