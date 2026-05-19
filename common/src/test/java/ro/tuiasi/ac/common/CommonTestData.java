package ro.tuiasi.ac.common;

/**
 * Utility class providing common test data for sensor and message tests.
 * Contains factory methods that create reusable test instances of
 * CameraFrame, LidarFrame, GyroscopeFrame, and SensorDataSet.
 *
 * <p>Using this utility class helps maintain consistent test data
 * across multiple test classes and reduces code duplication.
 *
 * @author Your Name
 */
public final class CommonTestData {

    /** Camera frame size (width and height). */
    private static final int FRAME_SIZE = 400;

    /** LiDAR frame size (width and height). */
    private static final int LIDAR_SIZE = 100;

    /** Gyroscope heading value for test data. */
    private static final double GYRO_HEADING = 90.0;

    /** Gyroscope angular velocity for test data. */
    private static final double GYRO_VELOCITY = 10.0;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private CommonTestData() {
        // Utility class - no instantiation needed
    }

    /**
     * Creates a CameraFrame with default dimensions (400x400).
     * All color channels are initialized with empty arrays.
     *
     * @return a new CameraFrame instance with default dimensions
     */
    public static CameraFrame cameraFrame() {
        return new CameraFrame(
                FRAME_SIZE,
                FRAME_SIZE,
                new int[FRAME_SIZE][FRAME_SIZE],
                new int[FRAME_SIZE][FRAME_SIZE],
                new int[FRAME_SIZE][FRAME_SIZE]
        );
    }

    /**
     * Creates a LidarFrame with default dimensions (100x100).
     * Distance array is initialized with zeros.
     *
     * @return a new LidarFrame instance with default dimensions
     */
    public static LidarFrame lidarFrame() {
        return new LidarFrame(
                LIDAR_SIZE,
                LIDAR_SIZE,
                new double[LIDAR_SIZE][LIDAR_SIZE]
        );
    }

    /**
     * Creates a GyroscopeFrame with predefined heading and velocity.
     * Heading is set to 90.0 degrees, angular velocity to 10.0 degrees/second.
     *
     * @return a new GyroscopeFrame instance with predefined values
     */
    public static GyroscopeFrame gyroscopeFrame() {
        return new GyroscopeFrame(GYRO_HEADING, GYRO_VELOCITY);
    }

    /**
     * Creates a complete SensorDataSet containing all sensor frames.
     * Uses the factory methods for individual frames to build the dataset.
     *
     * @return a new SensorDataSet containing camera, LiDAR, and gyroscope data
     * @see #cameraFrame()
     * @see #lidarFrame()
     * @see #gyroscopeFrame()
     */
    public static SensorDataSet sensorDataSet() {
        return new SensorDataSet(
                cameraFrame(),
                lidarFrame(),
                lidarFrame(),
                gyroscopeFrame()
        );
    }
}
