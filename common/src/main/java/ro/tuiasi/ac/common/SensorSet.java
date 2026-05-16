package ro.tuiasi.ac.common;

/**
 * Container for all sensors attached to the robot.
 * 
 * @author Your Name
 */
public class SensorSet {

	/** Camera sensor for visual detection */
	public final CameraSensor cameraSensor;

	/** Left-facing LiDAR sensor for obstacle detection */
	public final LidarSensor leftLidarSensor;

	/** Right-facing LiDAR sensor for obstacle detection */
	public final LidarSensor rightLidarSensor;

	/** Gyroscope sensor for orientation tracking */
	public final GyroscopeSensor gyroscopeSensor;

	/**
	 * Creates a new sensor set with the specified sensors.
	 * 
	 * @param cameraSensor     The camera sensor
	 * @param leftLidarSensor  The left LiDAR sensor
	 * @param rightLidarSensor The right LiDAR sensor
	 * @param gyroscopeSensor  The gyroscope sensor
	 */
	public SensorSet(CameraSensor cameraSensor, LidarSensor leftLidarSensor, LidarSensor rightLidarSensor,
			GyroscopeSensor gyroscopeSensor) {
		this.cameraSensor = cameraSensor;
		this.leftLidarSensor = leftLidarSensor;
		this.rightLidarSensor = rightLidarSensor;
		this.gyroscopeSensor = gyroscopeSensor;
	}

	/**
	 * Collects current data from all sensors.
	 * 
	 * @return SensorDataSet containing readings from all sensors
	 */
	public SensorDataSet collectData() {
		return new SensorDataSet(cameraSensor.readData(), leftLidarSensor.readData(), rightLidarSensor.readData(),
				gyroscopeSensor.readData());
	}
}