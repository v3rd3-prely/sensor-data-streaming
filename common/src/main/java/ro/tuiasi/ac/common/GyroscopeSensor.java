package ro.tuiasi.ac.common;

/**
 * Simulated gyroscope sensor that provides heading and angular velocity
 * readings.
 * 
 * @author Your Name
 */
public class GyroscopeSensor implements Sensor<GyroscopeFrame> {

	/** Unique identifier for this sensor */
	private final String id;

	/** Current heading in degrees (normalized to 0-360 range) */
	private double headingDegrees;

	/** Current angular velocity in degrees per second */
	private double angularVelocityDegreesPerSecond;

	/**
	 * Creates a new gyroscope sensor with the specified ID. Initial heading is 0
	 * degrees and angular velocity is 0.
	 * 
	 * @param id Unique identifier for this sensor
	 */
	public GyroscopeSensor(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public SensorType getType() {
		return SensorType.GYROSCOPE;
	}

	/**
	 * Returns the current gyroscope reading.
	 * 
	 * @return GyroscopeFrame containing current heading and angular velocity
	 */
	@Override
	public GyroscopeFrame readData() {
		return new GyroscopeFrame(headingDegrees, angularVelocityDegreesPerSecond);
	}

	/**
	 * Updates the gyroscope state with new values. Heading is automatically
	 * normalized to the range [0, 360).
	 * 
	 * @param headingDegrees                  New heading in degrees
	 * @param angularVelocityDegreesPerSecond New angular velocity in degrees/second
	 */
	public void update(double headingDegrees, double angularVelocityDegreesPerSecond) {
		this.headingDegrees = normalizeDegrees(headingDegrees);
		this.angularVelocityDegreesPerSecond = angularVelocityDegreesPerSecond;
	}

	/**
	 * Normalizes an angle to the range [0, 360).
	 * 
	 * @param degrees Angle in degrees
	 * @return Normalized angle between 0 and 360
	 */
	private double normalizeDegrees(double degrees) {
		double result = degrees % 360;
		return result < 0 ? result + 360 : result;
	}
}