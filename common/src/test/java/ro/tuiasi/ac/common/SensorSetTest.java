package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SensorSetTest {

    @Test
    void collectDataShouldReturnDataFromAllSensors() {
        SensorSet sensorSet = new SensorSet(
                new CameraSensor("camera-1", 100, 150),
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