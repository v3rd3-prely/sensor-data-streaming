package ro.tuiasi.ac.common;

/**
 * A generic interface representing a sensor that can read data of type T.
 *
 * <p>This interface defines the contract for all sensor implementations in the
 * system. Each sensor provides a unique identifier, a sensor type
 * classification, and the ability to read data from the sensor. The data type
 * is generic, allowing different sensors to return different data types (e.g.,
 * {@link CameraFrame}, temperature readings, distance measurements, etc.).
 *
 * <p>Implementations of this interface may represent:
 * <ul>
 * <li>Physical sensors (cameras, thermometers, distance sensors, etc.)</li>
 * <li>Simulated sensors for testing and development</li>
 * <li>Virtual sensors that aggregate or process data from other sensors</li>
 * </ul>
 *
 * <p>Thread-safety: Implementations should document their thread-safety
 * behavior. Generally, sensor reading methods should be thread-safe or properly
 * synchronized if called concurrently.
 *
 * @param <T> The type of data this sensor produces (e.g., CameraFrame, Double,
 *            String)
 * @author Your Name
 * @version 1.0
 * @see SensorType
 * @see CameraSensor
 */
public interface Sensor<T> {

    /**
     * Returns the unique identifier of this sensor.
     *
     * <p>The identifier should be unique across all sensors in the system to
     * enable proper identification and routing of sensor data. Common formats
     * include UUID strings, descriptive names with numbers, or hierarchical
     * paths.
     *
     * @return The sensor's unique identifier string
     */
    String getId();

    /**
     * Returns the classification type of this sensor.
     *
     * <p>The sensor type categorizes the sensor's function and helps determine
     * how to process its data. Examples include:
     * <ul>
     * <li>{@link SensorType#CAMERA} - Visual/image sensors</li>
     * </ul>
     *
     * @return The {@link SensorType} enumeration value for this sensor
     */
    SensorType getType();

    /**
     * Reads the current data from this sensor.
     *
     * <p>This method performs an actual sensor reading and returns the current
     * sensor value. For physical sensors, this may involve hardware
     * communication, which could take varying amounts of time. For simulated
     * sensors, this typically generates synthetic data immediately.
     *
     * <p>Implementations should:
     * <ul>
     * <li>Be as efficient as possible to support high-frequency polling</li>
     * <li>Handle hardware errors gracefully (e.g., by throwing appropriate
     * exceptions)</li>
     * <li>Document any side effects or state changes</li>
     * <li>Consider caching strategies if reading is expensive</li>
     * </ul>
     *
     * @return The current sensor reading of type T
     */
    T readData();
}
