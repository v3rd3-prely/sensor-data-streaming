package ro.tuiasi.ac.common;

public record LidarFrame(
        int width,
        int height,
        double[][] distancesCm
) {}