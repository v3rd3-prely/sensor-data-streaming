package ro.tuiasi.ac.common;

public record CameraFrame(
        int width,
        int height,
        int[][] red,
        int[][] green,
        int[][] blue
) {}