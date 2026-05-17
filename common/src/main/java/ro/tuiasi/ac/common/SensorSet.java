package ro.tuiasi.ac.common;

/**
 * Container for all sensors attached to the robot.
 *
 * @author Your Name
 */
public class SensorSet {

    /** Camera sensor for visual detection. */
    private final CameraSensor cameraSensor;

    /** Left-facing LiDAR sensor for obstacle detection. */
    private final LidarSensor leftLidarSensor;

    /** Right-facing LiDAR sensor for obstacle detection. */
    private final LidarSensor rightLidarSensor;

    /** Gyroscope sensor for orientation tracking. */
    private final GyroscopeSensor gyroscopeSensor;

    /**
     * Creates a new sensor set with the specified sensors.
     *
     * @param cameraSensorParam The camera sensor
     * @param leftLidarSensorParam The left LiDAR sensor
     * @param rightLidarSensorParam The right LiDAR sensor
     * @param gyroscopeSensorParam The gyroscope sensor
     */
    public SensorSet(final CameraSensor cameraSensorParam,
            final LidarSensor leftLidarSensorParam,
            final LidarSensor rightLidarSensorParam,
            final GyroscopeSensor gyroscopeSensorParam) {
        this.cameraSensor = cameraSensorParam;
        this.leftLidarSensor = leftLidarSensorParam;
        this.rightLidarSensor = rightLidarSensorParam;
        this.gyroscopeSensor = gyroscopeSensorParam;
    }

    /**
     * Returns the camera sensor.
     *
     * @return The camera sensor
     */
    public CameraSensor getCameraSensor() {
        return cameraSensor;
    }

    /**
     * Returns the left LiDAR sensor.
     *
     * @return The left LiDAR sensor
     */
    public LidarSensor getLeftLidarSensor() {
        return leftLidarSensor;
    }

    /**
     * Returns the right LiDAR sensor.
     *
     * @return The right LiDAR sensor
     */
    public LidarSensor getRightLidarSensor() {
        return rightLidarSensor;
    }

    /**
     * Returns the gyroscope sensor.
     *
     * @return The gyroscope sensor
     */
    public GyroscopeSensor getGyroscopeSensor() {
        return gyroscopeSensor;
    }

    /**
     * Collects current data from all sensors.
     *
     * @return SensorDataSet containing readings from all sensors
     */
    public SensorDataSet collectData() {
        return new SensorDataSet(
                cameraSensor.readData(),
                leftLidarSensor.readData(),
                rightLidarSensor.readData(),
                gyroscopeSensor.readData());
    }
}
