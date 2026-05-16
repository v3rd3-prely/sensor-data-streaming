package ro.tuiasi.ac.common;

import java.util.Random;

/**
 * Simulated LiDAR sensor that generates random distance measurements.
 * 
 * @author Your Name
 */
public class LidarSensor implements Sensor<LidarFrame> {

	/** Unique identifier for this sensor */
	private final String id;

	/** Width of the LiDAR scan in points */
	private static final int WIDTH = 100;

	/** Height of the LiDAR scan in points */
	private static final int HEIGHT = 100;

	/** Random number generator for simulated distance values */
	private final Random random = new Random();

	/**
	 * Creates a new LiDAR sensor with the specified ID.
	 * 
	 * @param id Unique identifier for this sensor
	 */
	public LidarSensor(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public SensorType getType() {
		return SensorType.LIDAR;
	}

	/**
	 * Generates a simulated LiDAR frame with random distance measurements.
	 * Distances range from 50 to 500 cm.
	 * 
	 * @return LidarFrame containing a 100x100 grid of random distances
	 */
	@Override
	public LidarFrame readData() {
		double[][] distances = new double[HEIGHT][WIDTH];

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				distances[y][x] = 50 + random.nextDouble() * 450;
			}
		}

		return new LidarFrame(WIDTH, HEIGHT, distances);
	}
}