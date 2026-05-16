package ro.tuiasi.ac.common;

public record GyroscopeFrame(
        double headingDegrees,
        double angularVelocityDegreesPerSecond
) {}