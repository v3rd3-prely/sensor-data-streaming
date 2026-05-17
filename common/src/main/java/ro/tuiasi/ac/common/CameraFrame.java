package ro.tuiasi.ac.common;

/**
 * A record representing a camera frame with separate RGB color channel
 * matrices.
 *
 * <p>This record encapsulates image data as three two-dimensional integer
 * arrays for red, green, and blue color channels. Each channel matrix has
 * dimensions [height][width] where each element represents the pixel intensity
 * value for that color component.
 *
 * <p>This is a Java record (since Java 14/16), which provides:
 * <ul>
 * <li>Automatic constructor generation</li>
 * <li>Immutable data structure</li>
 * <li>Automatic equals(), hashCode(), and toString() methods</li>
 * <li>Component accessor methods: width(), height(), red(), green(),
 * blue()</li>
 * </ul>
 *
 * <p>Thread-safety: This record is immutable and therefore thread-safe.
 *
 * <p>Usage example:
 * <pre>
 * // Create a frame
 * int width = 640;
 * int height = 480;
 * int[][] red = new int[height][width];
 * int[][] green = new int[height][width];
 * int[][] blue = new int[height][width];
 *
 * // Populate pixel values (0-255 for 8-bit color)
 * red[0][0] = 255; // Red pixel at top-left corner
 * green[0][0] = 0;
 * blue[0][0] = 0;
 *
 * CameraFrame frame = new CameraFrame(width, height, red, green, blue);
 *
 * // Access components
 * int frameWidth = frame.width();
 * int frameHeight = frame.height();
 * int pixelRed = frame.red()[y][x];
 * </pre>
 *
 * <p>Note: This record does not perform validation on array dimensions.
 * Callers should ensure all channel arrays have dimensions [height][width]
 * to avoid {@link ArrayIndexOutOfBoundsException} when accessing pixels.
 *
 * @param width  The width of the camera frame in pixels. Should be positive
 *               and match the second dimension of each color channel array.
 * @param height The height of the camera frame in pixels. Should be positive
 *               and match the first dimension of each color channel array.
 * @param red    Two-dimensional array containing red channel pixel values.
 *               Array dimensions should be [height][width] with typical
 *               values ranging from 0-255 for 8-bit color depth.
 * @param green  Two-dimensional array containing green channel pixel values.
 *               Array dimensions should be [height][width] with typical
 *               values ranging from 0-255 for 8-bit color depth.
 * @param blue   Two-dimensional array containing blue channel pixel values.
 *               Array dimensions should be [height][width] with typical
 *               values ranging from 0-255 for 8-bit color depth.
 * @author Your Name
 * @version 1.0
 * @see #width()
 * @see #height()
 * @see #red()
 * @see #green()
 * @see #blue()
 */
public record CameraFrame(
        int width,
        int height,
        int[][] red,
        int[][] green,
        int[][] blue) {
}
