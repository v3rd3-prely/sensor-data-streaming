package ro.tuiasi.ac.common;

public class Hello {
//	public static void sayHi() {
//		System.out.println("Hello from common.");

	public static void main(String[] args) {
		CameraSensor camera = new CameraSensor("camera-1", 100, 150);
		LidarSensor lidar = new LidarSensor("lidar-front");
		GyroscopeSensor gyro = new GyroscopeSensor("gyro-1");

		CameraFrame cameraFrame = camera.readData();
		LidarFrame lidarFrame = lidar.readData();

		gyro.update(90, 15);
		GyroscopeFrame gyroFrame = gyro.readData();

		System.out.println(cameraFrame.width());
		System.out.println(lidarFrame.distancesCm()[0][0]);
		System.out.println(gyroFrame.headingDegrees());
	}
}
