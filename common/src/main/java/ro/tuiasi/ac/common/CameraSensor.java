package ro.tuiasi.ac.common;

public class CameraSensor implements Sensor<CameraFrame> {

    private final String id;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int TARGET_SIZE = 20;  //dim patrat

    //coordonate patrat
    private int targetX;
    private int targetY;

    public CameraSensor(String id, int targetX, int targetY) {
        this.id = id;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SensorType getType() {
        return SensorType.CAMERA;
    }

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

    public void setTargetPosition(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }
}