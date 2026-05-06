package ro.tuiasi.ac.common;


public record SensorDataSet(
      
        CameraFrame cameraFrame,
        LidarFrame leftLidarFrame,
        LidarFrame rightLidarFrame,
        GyroscopeFrame gyroscopeFrame
       
) {}