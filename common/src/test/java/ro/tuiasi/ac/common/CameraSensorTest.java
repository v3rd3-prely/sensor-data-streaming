package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CameraSensorTest {

    @Test
    void shouldReturnCorrectIdAndType() {
        CameraSensor sensor = new CameraSensor("camera-1", 100, 150);

        assertEquals("camera-1", sensor.getId());
        assertEquals(SensorType.CAMERA, sensor.getType());
    }

    @Test
    void readDataShouldCreate400x400Frame() {
        CameraSensor sensor = new CameraSensor("camera-1", 100, 150);

        CameraFrame frame = sensor.readData();

        assertEquals(400, frame.width());
        assertEquals(400, frame.height());
        assertEquals(400, frame.red().length);
        assertEquals(400, frame.green().length);
        assertEquals(400, frame.blue().length);
    }

    @Test
    void readDataShouldDrawRedSquareAtTargetPosition() {
        CameraSensor sensor = new CameraSensor("camera-1", 100, 150);

        CameraFrame frame = sensor.readData();

        assertEquals(255, frame.red()[150][100]);
        assertEquals(0, frame.green()[150][100]);
        assertEquals(0, frame.blue()[150][100]);
    }

    @Test
    void setTargetPositionShouldUpdateCoordinates() {
        CameraSensor sensor = new CameraSensor("camera-1", 10, 20);

        sensor.setTargetPosition(50, 60);

        assertEquals(50, sensor.getTargetX());
        assertEquals(60, sensor.getTargetY());
    }
}