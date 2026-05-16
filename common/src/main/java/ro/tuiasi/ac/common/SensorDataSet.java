package ro.tuiasi.ac.common;

/**
 * Container for a complete set of sensor readings at a single point in time.
 * 
 * @param cameraFrame     The camera image data
 * @param leftLidarFrame  The left LiDAR scan data
 * @param rightLidarFrame The right LiDAR scan data
 * @param gyroscopeFrame  The gyroscope orientation data
 * @author Your Name
 */
public record SensorDataSet(CameraFrame cameraFrame, LidarFrame leftLidarFrame, LidarFrame rightLidarFrame,
		GyroscopeFrame gyroscopeFrame) {
}