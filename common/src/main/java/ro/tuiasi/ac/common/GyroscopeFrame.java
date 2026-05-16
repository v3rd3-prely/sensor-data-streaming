package ro.tuiasi.ac.common;

/**
 * Represents a gyroscope reading with heading and angular velocity.
 * 
 * @param headingDegrees                  Current orientation in degrees (0-360)
 * @param angularVelocityDegreesPerSecond Rate of rotation in degrees per second
 * @author Your Name
 */
public record GyroscopeFrame(double headingDegrees, double angularVelocityDegreesPerSecond) {
}