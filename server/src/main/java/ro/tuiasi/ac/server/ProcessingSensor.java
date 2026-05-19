package ro.tuiasi.ac.server;

import ro.tuiasi.ac.common.*;

public class ProcessingSensor {
	private static final double lidarThresh = 5;
	private static final int redThresh = 200; // un pixel este rosu daca red> 200 green < 50, blue < 50
	public static Command processSensorDataSet(SensorDataSet data) {
		double[][] leftLidarVals = data.leftLidarFrame().distancesCm();
		double[][] rightLidarVals = data.rightLidarFrame().distancesCm();
		
		 boolean obstacleLeft = false;
	     boolean obstacleRight = false;

	        // verificare stanga
	        for(int i = 0; i < leftLidarVals.length; i++) {
	            for(int j = 0; j < leftLidarVals[i].length; j++) {

	                if(leftLidarVals[i][j] < lidarThresh) {
	                    obstacleLeft = true;
	                }
	            }
	        }

	        // verificare dreapta
	        for(int i = 0; i < rightLidarVals.length; i++) {
	            for(int j = 0; j < rightLidarVals[i].length; j++) {

	                if(rightLidarVals[i][j] < lidarThresh) {
	                    obstacleRight = true;
	                }
	            }
	        }
	        
	     // verificare in stang
//	        for(int i = 0; i < leftLidarVals.length; i++) {
//	            for(int j = 0; j < leftLidarVals[i].length; j++) {
//
//	                if(leftLidarVals[i][j] < lidarThresh) {
//	                    obstacleLeft = true;
//	                }
//	            }
//	        }

	        // decizie
	        if(obstacleLeft) {
	        	System.out.println("Obstacle on the left side detected.");
	            return new MoveCommand(MoveDirection.RIGHT, 10);
	        }

	        if(obstacleRight) {
	        	System.out.println("Obstacle on the right side detected.");
	            return new MoveCommand(MoveDirection.LEFT, 10);
	        }
	        
	       
    		int[][] red = data.cameraFrame().red();
    		int[][] green = data.cameraFrame().green();
    		int[][] blue = data.cameraFrame().blue();
    		
    		int width = data.cameraFrame().width();
    		int height = data.cameraFrame().height();
    		
    		for(int j = 0; j< height; j++) {
    			for(int i = 0; i< width; i++) {
    				if(red[j][i] > redThresh && green[j][i] < 50 && blue[j][i] < 50) {
    					int targetX = i;
    					int targetY = j;
    					if(targetX + 10 < width/2) {
    						System.out.println("Rotation left towards the target.");
    						return new RotateCommand(RotateDirection.LEFT, 10);
    					}else if(targetX - 10 > width/2) {
    						System.out.println("Rotation right towards the target.");
    						return new RotateCommand(RotateDirection.RIGHT, 10);
    					}else if(targetY - 25 < height) {
    						System.out.println("Moving towards the target.");
    						return new MoveCommand(MoveDirection.FRONT, 10);
    					}else {
    						System.out.println("Target reached.");
    						return new StopCommand();
    					}
    				}
    					
    			}
    		}
	    		
	    	

	        return new StopCommand();
	    }
	
	
}
