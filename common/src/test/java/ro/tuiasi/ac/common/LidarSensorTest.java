package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LidarSensorTest {

    @Test
    void shouldReturnCorrectIdAndType() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        assertEquals("lidar-left", sensor.getId());
        assertEquals(SensorType.LIDAR, sensor.getType());
    }

    @Test
    void readDataShouldCreate100x100Frame() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        LidarFrame frame = sensor.readData();

        assertEquals(100, frame.width());
        assertEquals(100, frame.height());
        assertEquals(100, frame.distancesCm().length);
        assertEquals(100, frame.distancesCm()[0].length);
    }

    @Test
    void readDataShouldGenerateDistancesBetween50And500Cm() {
        LidarSensor sensor = new LidarSensor("lidar-left");

        LidarFrame frame = sensor.readData();

        for (int y = 0; y < frame.height(); y++) {
            for (int x = 0; x < frame.width(); x++) {
                double distance = frame.distancesCm()[y][x];

                assertTrue(distance >= 50.0);
                assertTrue(distance <= 500.0);
            }
        }
    }
}