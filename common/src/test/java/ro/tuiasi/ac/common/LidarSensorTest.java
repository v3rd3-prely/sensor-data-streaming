package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link LidarSensor}.
 * Verifies sensor identification, frame dimension correctness, and
 * distance value range validation.
 *
 * @author Your Name
 */
public final class LidarSensorTest {

    /** LiDAR frame size (width and height). */
    private static final int LIDAR_SIZE = 100;

    /** Minimum expected distance in centimeters. */
    private static final double MIN_DISTANCE_CM = 50.0;

    /** Maximum expected distance in centimeters. */
    private static final double MAX_DISTANCE_CM = 500.0;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private LidarSensorTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that the LiDAR sensor returns the correct identifier and type.
     * Verifies that the sensor ID matches the constructor argument and that
     * the sensor type is correctly identified as LIDAR.
     */
    @Test
    void shouldReturnCorrectIdAndType() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        assertEquals("lidar-left", sensor.getId());
        assertEquals(SensorType.LIDAR, sensor.getType());
    }

    /**
     * Tests that the LiDAR sensor generates a frame with correct dimensions.
     * Verifies that the frame width and height are both 100 pixels and that
     * the distance array has the expected dimensions.
     *
     * @see LidarSensor#readData()
     * @see LidarFrame#width()
     * @see LidarFrame#height()
     * @see LidarFrame#distancesCm()
     */
    @Test
    void readDataShouldCreateFrameWithCorrectSize() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        LidarFrame frame = sensor.readData();

        assertEquals(LIDAR_SIZE, frame.width());
        assertEquals(LIDAR_SIZE, frame.height());
        assertEquals(LIDAR_SIZE, frame.distancesCm().length);
        assertEquals(LIDAR_SIZE, frame.distancesCm()[0].length);
    }

    /**
     * Tests that all distance measurements fall within expected range.
     * Verifies that every distance value in the generated LiDAR frame
     * is between the minimum and maximum expected values, inclusive.
     *
     * @see LidarSensor#readData()
     * @see LidarFrame#distancesCm()
     */
    @Test
    void readDataShouldGenerateDistancesWithinExpectedRange() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        LidarFrame frame = sensor.readData();

        for (int y = 0; y < frame.height(); y++) {
            for (int x = 0; x < frame.width(); x++) {
                double distance = frame.distancesCm()[y][x];

                assertTrue(distance >= MIN_DISTANCE_CM);
                assertTrue(distance <= MAX_DISTANCE_CM);
            }
        }
    }
}
