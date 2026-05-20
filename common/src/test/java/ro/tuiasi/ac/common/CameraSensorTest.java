package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CameraSensor}.
 * Verifies camera sensor identification, frame generation,
 * and target positioning.
 *
 * @author Your Name
 */
public final class CameraSensorTest {

    /** Default target X coordinate for testing. */
    private static final int DEFAULT_TARGET_X = 100;

    /** Default target Y coordinate for testing. */
    private static final int DEFAULT_TARGET_Y = 150;

    /** Camera frame size (width and height). */
    private static final int FRAME_SIZE = 400;

    /** Red pixel value for target detection. */
    private static final int RED_PIXEL = 255;

    /** Initial target X coordinate for setter test. */
    private static final int INIT_TARGET_X = 10;

    /** Initial target Y coordinate for setter test. */
    private static final int INIT_TARGET_Y = 20;

    /** New target X coordinate after update. */
    private static final int NEW_TARGET_X = 50;

    /** New target Y coordinate after update. */
    private static final int NEW_TARGET_Y = 60;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private CameraSensorTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that the camera sensor returns
     * the correct identifier and sensor type.
     * Verifies that the sensor ID matches the constructor argument and that the
     * sensor type is correctly identified as CAMERA.
     */
    @Test
    void shouldReturnCorrectIdAndType() {
        CameraSensor sensor = new CameraSensor(
                "camera-1", DEFAULT_TARGET_X, DEFAULT_TARGET_Y);

        assertEquals("camera-1", sensor.getId());
        assertEquals(SensorType.CAMERA, sensor.getType());
    }

    /**
     * Tests that the camera sensor generates a frame with correct dimensions.
     * Verifies that the frame width and height are both 400 pixels and that
     * all color channel arrays have the expected dimensions.
     *
     * @see CameraSensor#readData()
     * @see CameraFrame#width()
     * @see CameraFrame#height()
     */
    @Test
    void readDataShouldCreateFrameWithCorrectSize() {
        CameraSensor sensor = new CameraSensor(
                "camera-1", DEFAULT_TARGET_X, DEFAULT_TARGET_Y);

        CameraFrame frame = sensor.readData();

        assertEquals(FRAME_SIZE, frame.width());
        assertEquals(FRAME_SIZE, frame.height());
        assertEquals(FRAME_SIZE, frame.red().length);
        assertEquals(FRAME_SIZE, frame.green().length);
        assertEquals(FRAME_SIZE, frame.blue().length);
    }

    /**
     * Tests that a red square is drawn at the
     * target position in the camera frame.
     * Verifies that at the target coordinates, the red channel value
     * is 255 while green and blue channels are 0, indicating a pure red pixel.
     *
     * @see CameraSensor#readData()
     * @see CameraFrame#red()
     * @see CameraFrame#green()
     * @see CameraFrame#blue()
     */
    @Test
    void readDataShouldDrawRedSquareAtTargetPosition() {
        CameraSensor sensor = new CameraSensor(
                "camera-1", DEFAULT_TARGET_X, DEFAULT_TARGET_Y);

        CameraFrame frame = sensor.readData();

        assertEquals(RED_PIXEL, frame.red()
                [DEFAULT_TARGET_Y][DEFAULT_TARGET_X]);
        assertEquals(0, frame.green()[DEFAULT_TARGET_Y][DEFAULT_TARGET_X]);
        assertEquals(0, frame.blue()[DEFAULT_TARGET_Y][DEFAULT_TARGET_X]);
    }

    /**
     * Tests that the target position is correctly updated.
     * Verifies that after calling
     * {@link CameraSensor#setTargetPosition(int, int)},
     * both getter methods return the updated coordinates.
     *
     * @see CameraSensor#setTargetPosition(int, int)
     * @see CameraSensor#getTargetX()
     * @see CameraSensor#getTargetY()
     */
    @Test
    void setTargetPositionShouldUpdateCoordinates() {
        CameraSensor sensor = new CameraSensor(
                "camera-1", INIT_TARGET_X, INIT_TARGET_Y);

        sensor.setTargetPosition(NEW_TARGET_X, NEW_TARGET_Y);

        assertEquals(NEW_TARGET_X, sensor.getTargetX());
        assertEquals(NEW_TARGET_Y, sensor.getTargetY());
    }
}
