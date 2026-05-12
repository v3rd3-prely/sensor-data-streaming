package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;

public class Robot {
	private final SensorSet sensorSet;

	public Robot(SensorSet sensorSet) {
		this.sensorSet = sensorSet;
	}

	public SensorDataSet collectData() {
		return sensorSet.collectData();
	}

}
