package ro.tuiasi.ac.common;

public interface Sensor<T> {
    String getId();
    SensorType getType();
    T readData();
}