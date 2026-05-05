package ro.tuiasi.ac.common;

public class GyroscopeSensor implements Sensor<GyroscopeFrame> {

    private final String id;

    private double headingDegrees;
    private double angularVelocityDegreesPerSecond;

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

    @Override
    public GyroscopeFrame readData() {
        return new GyroscopeFrame(
                headingDegrees,
                angularVelocityDegreesPerSecond
        );
    }

    public void update(double headingDegrees, double angularVelocityDegreesPerSecond) {
        this.headingDegrees = normalizeDegrees(headingDegrees);
        this.angularVelocityDegreesPerSecond = angularVelocityDegreesPerSecond;
    }

    private double normalizeDegrees(double degrees) {
        double result = degrees % 360;
        return result < 0 ? result + 360 : result;
    }
}