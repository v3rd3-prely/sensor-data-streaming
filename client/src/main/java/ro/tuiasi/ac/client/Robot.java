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
			default: System.out.println("Move direction unknown.");
			}
			int newTargetX = sensorSet.cameraSensor.getTargetX()+ offsetX;
			int newTargetY = sensorSet.cameraSensor.getTargetY()+ offsetY;
			
			if(newTargetX < 0) {
				newTargetX = 0;
			}
			if(newTargetY < 0) {
				newTargetY = 0;
			}
			if(newTargetX > 400) {
				newTargetX = 400;
			}
			if(newTargetY < 400) {
				newTargetY = 400;
			}
			
			sensorSet.cameraSensor.setTargetPosition(newTargetX, newTargetY);
			System.out.println("Move command executed.");
		}else if(cmd instanceof RotateCommand rot) {
			RotateDirection dir = rot.getOrientation();
			float degrees = rot.getGrade();
			switch(dir) {
			case LEFT:
				break;
			case RIGHT:
				degrees = -degrees;
				break;
			default:
				System.out.println("Rotation direction unknown.");
			}
			int newTargetX = (int) (sensorSet.cameraSensor.getTargetX()+degrees);
			int newTargetY = sensorSet.cameraSensor.getTargetY();
			
			if(newTargetX < 0) {
				newTargetX = 0;
			}
			
			if(newTargetX > 400) {
				newTargetX = 400;
			}
			sensorSet.cameraSensor.setTargetPosition(newTargetX, newTargetY);
			sensorSet.gyroscopeSensor.update(degrees, degrees);
			System.out.println("Rotate command executed.");
		}else {
			System.out.println("Command unknown");
		}
	}

}
