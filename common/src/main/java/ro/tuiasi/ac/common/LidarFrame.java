package ro.tuiasi.ac.common;

/**
 * Represents a 2D LiDAR scan frame with distance measurements.
 *
 * @param width       Number of horizontal scan points
 * @param height      Number of vertical scan points
 * @param distancesCm 2D array of distance measurements in centimeters
 * @author Your Name
 */
public record LidarFrame(int width, int height, double[][] distancesCm) {
}
