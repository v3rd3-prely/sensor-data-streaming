package ro.tuiasi.ac.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import ro.tuiasi.ac.common.CameraFrame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * HTTP server that displays live camera frames from the robot.
 * Serves an HTML page with auto-refreshing image stream and handles
 * frame requests by converting CameraFrame objects to PNG images.
 *
 * <p>The server runs on port 8082 and provides two endpoints:
 * <ul>
 *   <li>/ - Serves an HTML page with JavaScript that refreshes the image
 *       every 100ms</li>
 *   <li>/frame - Returns the current camera frame as a PNG image</li>
 * </ul>
 *
 * @author Your Name
 */
public class ImageViewer {

    /** HTTP server port number. */
    private static final int SERVER_PORT = 8082;

    /** HTTP success status code. */
    private static final int HTTP_OK = 200;

    /** Bit shift for red channel (16 bits). */
    private static final int RED_SHIFT = 16;

    /** Bit shift for green channel (8 bits). */
    private static final int GREEN_SHIFT = 8;

    /** The most recently received camera frame, converted to BufferedImage. */
    private volatile BufferedImage latestImage;

    /**
     * Creates and starts the HTTP server on port 8082.
     * Registers the page handler for "/" and the image handler for "/frame".
     *
     * @throws IOException if the server cannot be created or started
     */
    public ImageViewer() throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(SERVER_PORT), 0);

        server.createContext("/", new PageHandler());
        server.createContext("/frame", new ImageHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("ImageViewer started on http://localhost:8082");
    }

    /**
     * Updates the displayed frame with new camera data.
     * Converts the CameraFrame to a BufferedImage and stores it for future
     * HTTP requests.
     *
     * @param frame the new camera frame to display
     */
    public void updateFrame(final CameraFrame frame) {
        latestImage = convertToImage(frame);
    }

    /**
     * Converts a CameraFrame object to a BufferedImage.
     * Maps RGB channel values from the frame's color matrices to pixel colors.
     *
     * @param frame the camera frame to convert
     * @return BufferedImage containing the converted image data
     */
    private BufferedImage convertToImage(final CameraFrame frame) {
        BufferedImage image = new BufferedImage(
                frame.width(), frame.height(), BufferedImage.TYPE_INT_RGB);

        int[][] red = frame.red();
        int[][] green = frame.green();
        int[][] blue = frame.blue();

        for (int y = 0; y < frame.height(); y++) {
            for (int x = 0; x < frame.width(); x++) {
                int r = red[y][x];
                int g = green[y][x];
                int b = blue[y][x];

                int rgb = (r << RED_SHIFT) | (g << GREEN_SHIFT) | b;

                image.setRGB(x, y, rgb);
            }
        }

        return image;
    }

    /**
     * HTTP handler for serving camera frame images.
     * Responds with the latest camera frame as a PNG image,
     * or a text message if no image is available.
     */
    private final class ImageHandler implements HttpHandler {

        /**
         * Handles HTTP GET requests to the /frame endpoint.
         * Returns the latest camera frame as a PNG image with
         * Content-Type image/png. If no frame has been received yet,
         * returns a text message.
         *
         * @param exchange the HTTP exchange containing the request and response
         * @throws IOException if an I/O error occurs while handling the request
         */
        @Override
        public void handle(final HttpExchange exchange) throws IOException {
            if (latestImage == null) {
                String response = "No image available yet";

                exchange.sendResponseHeaders(HTTP_OK, response.length());

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(latestImage, "png", outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(HTTP_OK, imageBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(imageBytes);
            os.close();
        }
    }

    /**
     * HTTP handler for serving the HTML page.
     * Returns an HTML document that displays the camera feed with auto-refresh.
     */
    private final class PageHandler implements HttpHandler {

        /**
         * Handles HTTP GET requests to the root endpoint.
         * Returns an HTML page with JavaScript that refreshes the camera image
         * every 100 milliseconds by appending a timestamp to prevent caching.
         *
         * @param exchange the HTTP exchange containing the request and response
         * @throws IOException if an I/O error occurs while handling the request
         */
        @Override
        public void handle(final HttpExchange exchange) throws IOException {
            String html = """
                <html>
                <head>
                    <title>Robot Camera</title>
                    <style>
                        body {
                            background: black;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }

                        img {
                            border: 2px solid white;
                        }
                    </style>
                </head>

                <body>
                    <img id="camera" width="400" height="400">

                    <script>
                        setInterval(() => {
                            document.getElementById("camera").src =
                                "/frame?t=" + new Date().getTime();
                        }, 100);
                    </script>
                </body>
                </html>
                """;

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(HTTP_OK, html.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }
}
