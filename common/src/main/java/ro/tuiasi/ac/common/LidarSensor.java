package ro.tuiasi.ac.common;

import java.util.Random;

public class LidarSensor implements Sensor<LidarFrame> {

    private final String id;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    private final Random random = new Random();

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