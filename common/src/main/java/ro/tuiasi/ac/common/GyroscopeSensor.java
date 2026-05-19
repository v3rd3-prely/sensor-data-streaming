package ro.tuiasi.ac.common;

/**
 * Simulated gyroscope sensor that provides heading and angular velocity
 * readings.
 *
 * @author Your Name
 */
public final class GyroscopeSensor implements Sensor<GyroscopeFrame> {

    /** Full circle in degrees. */
    private static final int FULL_CIRCLE_DEG = 360;

    /** Unique identifier for this sensor. */
    private final String id;

    /** Current heading in degrees (normalized to 0-360 range). */
    private double headingDegrees;

    /** Current angular velocity in degrees per second. */
    private double angularVelocityDegreesPerSecond;

    /**
     * Creates a new gyroscope sensor with the specified ID.
     * Initial heading is 0 degrees and angular velocity is 0.
     *
     * @param idParam Unique identifier for this sensor
     */
    public GyroscopeSensor(final String idParam) {
        this.id = idParam;
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
        return new GyroscopeFrame(
                headingDegrees, angularVelocityDegreesPerSecond);
    }

    /**
     * Updates the gyroscope state with new values.
     * Heading is automatically normalized to the range [0, 360).
     *
     * @param headingDegreesParam New heading in degrees
     * @param angularVelocityParam New angular velocity in degrees/second
     */
    public void update(final double headingDegreesParam,
            final double angularVelocityParam) {
        this.headingDegrees = normalizeDegrees(headingDegreesParam);
        this.angularVelocityDegreesPerSecond = angularVelocityParam;
    }

    /**
     * Normalizes an angle to the range [0, 360).
     *
     * @param degrees Angle in degrees
     * @return Normalized angle between 0 and 360
     */
    private double normalizeDegrees(final double degrees) {
        double result = degrees % FULL_CIRCLE_DEG;
        return result < 0 ? result + FULL_CIRCLE_DEG : result;
    }
}
