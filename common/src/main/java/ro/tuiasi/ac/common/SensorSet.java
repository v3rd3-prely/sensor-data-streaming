package ro.tuiasi.ac.common;

public class SensorSet {

	private final CameraSensor cameraSensor;
	private final LidarSensor leftLidarSensor;
	private final LidarSensor rightLidarSensor;
	private final GyroscopeSensor gyroscopeSensor;

	public SensorSet(CameraSensor cameraSensor, LidarSensor leftLidarSensor, LidarSensor rightLidarSensor,
			GyroscopeSensor gyroscopeSensor) {
		this.cameraSensor = cameraSensor;
		this.leftLidarSensor = leftLidarSensor;
		this.rightLidarSensor = rightLidarSensor;
		this.gyroscopeSensor = gyroscopeSensor;
	}

	public SensorDataSet collectData() {
		return new SensorDataSet(cameraSensor.readData(), leftLidarSensor.readData(), rightLidarSensor.readData(),
				gyroscopeSensor.readData());
	}
}