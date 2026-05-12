package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.*;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;

public class Robot {
	private final SensorSet sensorSet;
	private boolean	isSendingData;

	public Robot(SensorSet sensorSet) {
		this.sensorSet = sensorSet;
		this.isSendingData = false;
	}

	public SensorDataSet collectData() {
		return sensorSet.collectData();
	}
	
	public boolean isSending() {
		return this.isSendingData;
	}
	
	public void executeCommand(Command cmd) {
		if(cmd instanceof StartCommand) {
			this.isSendingData = true;
			System.out.println("Started sending data.");
		}else if (cmd instanceof StopCommand) {
			this.isSendingData = false;
			System.out.println("Stopped sending data.");
		}else if(cmd instanceof MoveCommand mov) {
			MoveDirection dir = mov.getDirection();
			float dist = mov.getDistance();
			int offsetX = 0, offsetY = 0;
			switch(dir) {
			case LEFT:
				offsetX += dist;
				break;
			case RIGHT:
				offsetX -= dist;
				break;
			case FRONT:
				offsetY += dist;
				break;
			case BACK:
				offsetY -= dist;
				break;
			default: System.out.println("Direction unknown.");
			}
		}else if(cmd instanceof RotateCommand) {
			
		}else {
			System.out.println("Command unknown");
		}
	}

}
