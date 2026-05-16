package ro.tuiasi.ac.common;

/**
 * A camera sensor implementation that generates simulated camera frames with a
 * visual target (red square) at a specified position.
 * 
 * <p>
 * This sensor implements the {@link Sensor} interface for {@link CameraFrame}
 * data types. It generates synthetic image data of a fixed size (400x400
 * pixels) with a red square marker representing the target position. The red
 * square has dimensions of 20x20 pixels and can be moved by updating its
 * coordinates.
 * 
 * <p>
 * This is a simulation sensor that doesn't require physical camera hardware.
 * All pixels outside the target area are black (RGB: 0,0,0), while pixels
 * within the target area are pure red (RGB: 255,0,0). The target square is
 * clipped to frame boundaries if positioned near the edges.
 * 
 * <p>
 * Thread-safety: This class is not thread-safe as the target position can be
 * modified while being read. External synchronization is required if instances
 * are shared across threads.
 * 
 * <p>
 * Usage example:
 * 
 * <pre>
 * // Create a camera sensor with target at center (200, 200)
 * CameraSensor sensor = new CameraSensor("camera-1", 200, 200);
 * 
 * // Read the current camera frame
 * CameraFrame frame = sensor.readData();
 * 
 * // Move the target to a new position
 * sensor.setTargetPosition(100, 300);
 * 
 * // Read again with new target position
 * CameraFrame updatedFrame = sensor.readData();
 * 
 * // Access sensor information
 * String sensorId = sensor.getId();
 * SensorType type = sensor.getType();
 * int currentTargetX = sensor.getTargetX();
 * int currentTargetY = sensor.getTargetY();
 * </pre>
 * 
 * @author Your Name
 * @version 1.0
 * @see Sensor
 * @see CameraFrame
 * @see SensorType
 */
public class CameraSensor implements Sensor<CameraFrame> {
	/**
	 * Unique identifier for this camera sensor instance.
	 */
	private final String id;
	/**
	 * Width of the generated camera frame in pixels. Constant value: 400 pixels.
	 */
	private static final int WIDTH = 400;
	/**
	 * Height of the generated camera frame in pixels. Constant value: 400 pixels.
	 */
	private static final int HEIGHT = 400;
	/**
	 * Size of the square target marker in pixels. Constant value: 20x20 pixels.
	 */
	private static final int TARGET_SIZE = 20; // dim patrat

	/**
	 * X-coordinate of the top-left corner of the target square. Valid range: 0 to
	 * (WIDTH - TARGET_SIZE)
	 */
	private int targetX;
	/**
	 * Y-coordinate of the top-left corner of the target square. Valid range: 0 to
	 * (HEIGHT - TARGET_SIZE)
	 */
	private int targetY;

	/**
	 * Constructs a new CameraSensor with the specified identifier and target
	 * position.
	 * 
	 * <p>
	 * The target position defines where the red square marker will appear in the
	 * generated camera frames. The target square has a fixed size of 20x20 pixels.
	 * 
	 * @param id      Unique identifier for this camera sensor
	 * @param targetX Initial X-coordinate of the target square's top-left corner
	 * @param targetY Initial Y-coordinate of the target square's top-left corner
	 */
	public CameraSensor(String id, int targetX, int targetY) {
		this.id = id;
		this.targetX = targetX;
		this.targetY = targetY;
	}

	/**
	 * Returns the unique identifier of this camera sensor.
	 * 
	 * @return The sensor ID string
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Returns the type of this sensor.
	 * 
	 * @return {@link SensorType#CAMERA}
	 */
	@Override
	public SensorType getType() {
		return SensorType.CAMERA;
	}

	/**
	 * Generates and returns a simulated camera frame.
	 * 
	 * <p>
	 * This method creates a 400x400 pixel RGB image where:
	 * <ul>
	 * <li>All pixels outside the target area are black (0,0,0)</li>
	 * <li>All pixels within the target area are red (255,0,0)</li>
	 * </ul>
	 * 
	 * <p>
	 * The target area is a square of size
	 * {@value #TARGET_SIZE}x{@value #TARGET_SIZE} pixels located at the current
	 * target coordinates. The target square is automatically clipped to frame
	 * boundaries if it would extend beyond the 400x400 frame dimensions.
	 * 
	 * @return A new {@link CameraFrame} instance containing the generated image
	 *         data
	 */
	@Override
	public CameraFrame readData() {
		int[][] red = new int[HEIGHT][WIDTH];
		int[][] green = new int[HEIGHT][WIDTH];
		int[][] blue = new int[HEIGHT][WIDTH];

		for (int y = targetY; y < targetY + TARGET_SIZE && y < HEIGHT; y++) {
			for (int x = targetX; x < targetX + TARGET_SIZE && x < WIDTH; x++) {
				if (x >= 0 && y >= 0) {
					red[y][x] = 255;
					green[y][x] = 0;
					blue[y][x] = 0;
				}
			}
		}

		return new CameraFrame(WIDTH, HEIGHT, red, green, blue);
	}

	/**
	 * Returns the current X-coordinate of the target square's top-left corner.
	 * 
	 * @return The target X-coordinate
	 */
	public int getTargetX() {
		return this.targetX;
	}

	/**
	 * Returns the current Y-coordinate of the target square's top-left corner.
	 * 
	 * @return The target Y-coordinate
	 */
	public int getTargetY() {
		return this.targetY;
	}

	/**
	 * Updates the position of the target square.
	 * 
	 * <p>
	 * The target square will appear at the new coordinates in subsequent calls to
	 * {@link #readData()}. No bounds checking is performed; coordinates may be
	 * negative or beyond frame boundaries, resulting in the target square being
	 * partially or completely clipped.
	 * 
	 * @param targetX New X-coordinate for the target square's top-left corner
	 * @param targetY New Y-coordinate for the target square's top-left corner
	 */
	public void setTargetPosition(int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
	}
}