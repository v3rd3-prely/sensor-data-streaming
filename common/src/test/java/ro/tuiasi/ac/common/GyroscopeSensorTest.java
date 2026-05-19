package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link GyroscopeSensor}.
 * Verifies sensor identification, data storage, and heading normalization
 * functionality.
 *
 * @author Your Name
 */
public final class GyroscopeSensorTest {

    /** Test heading value for storage test. */
    private static final double TEST_HEADING = 90.0;

    /** Test angular velocity for storage test. */
    private static final double TEST_VELOCITY = 15.0;

    /** Heading value that needs normalization (above 360). */
    private static final double HEADING_ABOVE_360 = 370.0;

    /** Expected normalized heading. */
    private static final double NORMALIZED_FROM_ABOVE = 10.0;

    /** Negative heading value. */
    private static final double NEGATIVE_HEADING = -30.0;

    /** Expected normalized heading from negative. */
    private static final double NORMALIZED_FROM_NEGATIVE = 330.0;

    /** Angular velocity for normalization tests. */
    private static final double TEST_VELOCITY_NORM = 10.0;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private GyroscopeSensorTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that the gyroscope sensor returns the correct identifier and type.
     * Verifies that the sensor ID matches the constructor argument and that
     * the sensor type is correctly identified as GYROSCOPE.
     */
    @Test
    void shouldReturnCorrectIdAndType() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        assertEquals("gyro-1", sensor.getId());
        assertEquals(SensorType.GYROSCOPE, sensor.getType());
    }

    /**
     * Tests that update correctly stores heading and angular velocity values.
     * Verifies that after calling update, readData returns a frame containing
     * the exact values provided.
     *
     * @see GyroscopeSensor#update(double, double)
     * @see GyroscopeSensor#readData()
     */
    @Test
    void updateShouldStoreHeadingAndAngularVelocity() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(TEST_HEADING, TEST_VELOCITY);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(TEST_HEADING, frame.headingDegrees());
        assertEquals(TEST_VELOCITY, frame.angularVelocityDegreesPerSecond());
    }

    /**
     * Tests that heading values greater than 360 degrees are normalized.
     * Verifies that a heading above 360 degrees is correctly normalized
     * to the equivalent angle within the 0-360 range.
     *
     * @see GyroscopeSensor#update(double, double)
     * @see GyroscopeFrame#headingDegrees()
     */
    @Test
    void updateShouldNormalizeHeadingGreaterThan360() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(HEADING_ABOVE_360, TEST_VELOCITY_NORM);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(NORMALIZED_FROM_ABOVE, frame.headingDegrees());
    }

    /**
     * Tests that negative heading values are correctly normalized.
     * Verifies that a negative heading is normalized to the equivalent
     * positive angle within the 0-360 range.
     *
     * @see GyroscopeSensor#update(double, double)
     * @see GyroscopeFrame#headingDegrees()
     */
    @Test
    void updateShouldNormalizeNegativeHeading() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(NEGATIVE_HEADING, TEST_VELOCITY_NORM);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(NORMALIZED_FROM_NEGATIVE, frame.headingDegrees());
    }
}
