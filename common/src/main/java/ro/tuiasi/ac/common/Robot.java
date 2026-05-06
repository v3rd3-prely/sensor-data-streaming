package ro.tuiasi.ac.common;

public class Robot {

   
    private final SensorSet sensorSet;

    public Robot(SensorSet sensorSet) {
        this.sensorSet = sensorSet;
    }

    public SensorDataSet collectData() {
        return sensorSet.collectData();
    }


}