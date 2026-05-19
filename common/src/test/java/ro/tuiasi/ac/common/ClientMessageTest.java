package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ClientMessage}.
 * Verifies message creation, field initialization, getter/setter functionality,
 * and string representation.
 *
 * @author Your Name
 */
public final class ClientMessageTest {

    /** Camera frame size (width and height). */
    private static final int FRAME_SIZE = 400;

    /** LiDAR frame size (width and height). */
    private static final int LIDAR_SIZE = 100;

    /** Gyroscope heading value for test data. */
    private static final double GYRO_HEADING = 90.0;

    /** Gyroscope angular velocity for test data. */
    private static final double GYRO_VELOCITY = 10.0;

    /** Test timestamp value. */
    private static final long TEST_TIMESTAMP = 999L;

    /** Test message ID. */
    private static final String TEST_ID = "123";

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private ClientMessageTest() {
        // Test class - no instantiation needed
    }

    /**
     * Creates a complete SensorDataSet with all required sensor frames.
     * This helper method provides realistic test data for message creation.
     *
     * @return a fully populated SensorDataSet with camera, LiDAR,
     *         and gyroscope data
     */
    private SensorDataSet createSensorDataSet() {
        CameraFrame cameraFrame = new CameraFrame(
                FRAME_SIZE,
                FRAME_SIZE,
                new int[FRAME_SIZE][FRAME_SIZE],
                new int[FRAME_SIZE][FRAME_SIZE],
                new int[FRAME_SIZE][FRAME_SIZE]
        );

        LidarFrame frontLidarFrame = new LidarFrame(
                LIDAR_SIZE,
                LIDAR_SIZE,
                new double[LIDAR_SIZE][LIDAR_SIZE]
        );

        LidarFrame sideLidarFrame = new LidarFrame(
                LIDAR_SIZE,
                LIDAR_SIZE,
                new double[LIDAR_SIZE][LIDAR_SIZE]
        );

        GyroscopeFrame gyroscopeFrame = new GyroscopeFrame(
                GYRO_HEADING,
                GYRO_VELOCITY
        );

        return new SensorDataSet(
                cameraFrame,
                frontLidarFrame,
                sideLidarFrame,
                gyroscopeFrame
        );
    }

    /**
     * Tests that the parameterized constructor properly initializes all fields.
     * Verifies that a unique ID is generated, content is correctly stored,
     * and the sent timestamp is set to a positive value.
     *
     * @see ClientMessage#ClientMessage(SensorDataSet)
     */
    @Test
    void constructorShouldInitializeFields() {
        SensorDataSet dataSet = createSensorDataSet();

        ClientMessage message = new ClientMessage(dataSet);

        assertNotNull(message.getId());
        assertEquals(dataSet, message.getContent());
        assertTrue(message.getSentTimestamp() > 0);
    }

    /**
     * Tests that setters and getters work correctly.
     * Verifies that each field can be set
     * and retrieved with the expected value.
     *
     * @see ClientMessage#setId(String)
     * @see ClientMessage#setContent(SensorDataSet)
     * @see ClientMessage#setSentTimestamp(long)
     */
    @Test
    void settersAndGettersShouldWork() {
        ClientMessage message = new ClientMessage();
        SensorDataSet dataSet = createSensorDataSet();

        message.setId(TEST_ID);
        message.setContent(dataSet);
        message.setSentTimestamp(TEST_TIMESTAMP);

        assertEquals(TEST_ID, message.getId());
        assertEquals(dataSet, message.getContent());
        assertEquals(TEST_TIMESTAMP, message.getSentTimestamp());
    }

    /**
     * Tests that the string representation contains key message information.
     * Verifies that the toString method includes the message ID and timestamp.
     *
     * @see ClientMessage#toString()
     */
    @Test
    void toStringShouldContainMessageInformation() {
        SensorDataSet dataSet = createSensorDataSet();

        ClientMessage message = new ClientMessage(dataSet);

        String result = message.toString();

        assertTrue(result.contains(message.getId()));
        assertTrue(result.contains("sent"));
    }
}
