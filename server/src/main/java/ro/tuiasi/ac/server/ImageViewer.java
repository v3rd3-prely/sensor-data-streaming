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

public class ImageViewer {

	private volatile BufferedImage latestImage;

	public ImageViewer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);

		server.createContext("/", new PageHandler());
		server.createContext("/frame", new ImageHandler());

		server.setExecutor(null);
		server.start();

		System.out.println("ImageViewer started on http://localhost:8082");
	}

	public void updateFrame(CameraFrame frame) {
		latestImage = convertToImage(frame);
	}

	private BufferedImage convertToImage(CameraFrame frame) {

		BufferedImage image = new BufferedImage(frame.width(), frame.height(), BufferedImage.TYPE_INT_RGB);

		int[][] red = frame.red();
		int[][] green = frame.green();
		int[][] blue = frame.blue();

		for (int y = 0; y < frame.height(); y++) {
			for (int x = 0; x < frame.width(); x++) {

				int r = red[y][x];
				int g = green[y][x];
				int b = blue[y][x];

				int rgb = (r << 16) | (g << 8) | b;

				image.setRGB(x, y, rgb);
			}
		}

		return image;
	}

	private class ImageHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {

			if (latestImage == null) {
				String response = "No image available yet";

				exchange.sendResponseHeaders(200, response.length());

				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();

				return;
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ImageIO.write(latestImage, "png", outputStream);

			byte[] imageBytes = outputStream.toByteArray();

			exchange.getResponseHeaders().set("Content-Type", "image/png");
			exchange.sendResponseHeaders(200, imageBytes.length);

			OutputStream os = exchange.getResponseBody();
			os.write(imageBytes);
			os.close();
		}
	}
	
	private class PageHandler implements HttpHandler {

	    @Override
	    public void handle(HttpExchange exchange) throws IOException {

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

	        exchange.sendResponseHeaders(200, html.getBytes().length);

	        OutputStream os = exchange.getResponseBody();
	        os.write(html.getBytes());
	        os.close();
	    }
	}
}





