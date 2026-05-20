package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link SensorSet}.
 * Verifies that sensor data collection aggregates readings from all
 * attached sensors correctly.
 *
 * @author Your Name
 */
public final class SensorSetTest {

    /** Default target X coordinate for camera sensor. */
    private static final int DEFAULT_TARGET_X = 100;

    /** Default target Y coordinate for camera sensor. */
    private static final int DEFAULT_TARGET_Y = 150;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private SensorSetTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that collectData returns a complete SensorDataSet containing
     * readings from all sensors in the set.
     * Verifies that the returned data set contains non-null frames for
     * camera, left LiDAR, right LiDAR, and gyroscope sensors.
     *
     * @see SensorSet#collectData()
     * @see SensorDataSet
     */
    @Test
    void collectDataShouldReturnDataFromAllSensors() {
        SensorSet sensorSet = new SensorSet(
                new CameraSensor("camera-1",
                        DEFAULT_TARGET_X, DEFAULT_TARGET_Y),
                new LidarSensor("lidar-left"),
                new LidarSensor("lidar-right"),
                new GyroscopeSensor("gyro-1")
        );

        SensorDataSet dataSet = sensorSet.collectData();

        assertNotNull(dataSet);
        assertNotNull(dataSet.cameraFrame());
        assertNotNull(dataSet.leftLidarFrame());
        assertNotNull(dataSet.rightLidarFrame());
        assertNotNull(dataSet.gyroscopeFrame());
    }
}
