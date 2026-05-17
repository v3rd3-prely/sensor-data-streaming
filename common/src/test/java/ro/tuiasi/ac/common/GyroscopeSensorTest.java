package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GyroscopeSensorTest {

    @Test
    void shouldReturnCorrectIdAndType() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        assertEquals("gyro-1", sensor.getId());
        assertEquals(SensorType.GYROSCOPE, sensor.getType());
    }

    @Test
    void updateShouldStoreHeadingAndAngularVelocity() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(90.0, 15.0);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(90.0, frame.headingDegrees());
        assertEquals(15.0, frame.angularVelocityDegreesPerSecond());
    }

    @Test
    void updateShouldNormalizeHeadingGreaterThan360() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(370.0, 10.0);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(10.0, frame.headingDegrees());
    }

    @Test
    void updateShouldNormalizeNegativeHeading() {
        GyroscopeSensor sensor = new GyroscopeSensor("gyro-1");

        sensor.update(-30.0, 10.0);
        GyroscopeFrame frame = sensor.readData();

        assertEquals(330.0, frame.headingDegrees());
    }
}