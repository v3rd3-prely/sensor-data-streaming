package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientMessageTest {

    private SensorDataSet createSensorDataSet() {
        CameraFrame cameraFrame = new CameraFrame(
                400,
                400,
                new int[400][400],
                new int[400][400],
                new int[400][400]
        );

        LidarFrame frontLidarFrame = new LidarFrame(
                100,
                100,
                new double[100][100]
        );

        LidarFrame sideLidarFrame = new LidarFrame(
                100,
                100,
                new double[100][100]
        );

        GyroscopeFrame gyroscopeFrame = new GyroscopeFrame(
                90.0,
                10.0
        );

        return new SensorDataSet(
                cameraFrame,
                frontLidarFrame,
                sideLidarFrame,
                gyroscopeFrame
        );
    }

    @Test
    void constructorShouldInitializeFields() {
        SensorDataSet dataSet = createSensorDataSet();

        ClientMessage message = new ClientMessage(dataSet);

        assertNotNull(message.getId());
        assertEquals(dataSet, message.getContent());
        assertTrue(message.getSentTimestamp() > 0);
    }

    @Test
    void settersAndGettersShouldWork() {
        ClientMessage message = new ClientMessage();
        SensorDataSet dataSet = createSensorDataSet();

        message.setId("123");
        message.setContent(dataSet);
        message.setSentTimestamp(999L);

        assertEquals("123", message.getId());
        assertEquals(dataSet, message.getContent());
        assertEquals(999L, message.getSentTimestamp());
    }

    @Test
    void toStringShouldContainMessageInformation() {
        SensorDataSet dataSet = createSensorDataSet();

        ClientMessage message = new ClientMessage(dataSet);

        String result = message.toString();

        assertTrue(result.contains(message.getId()));
        assertTrue(result.contains("sent"));
    }
}