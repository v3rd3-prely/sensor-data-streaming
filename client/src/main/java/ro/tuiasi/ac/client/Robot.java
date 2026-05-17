package ro.tuiasi.ac.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.tuiasi.ac.common.CameraSensor;
import ro.tuiasi.ac.common.Command;
import ro.tuiasi.ac.common.MoveCommand;
import ro.tuiasi.ac.common.MoveDirection;
import ro.tuiasi.ac.common.RotateCommand;
import ro.tuiasi.ac.common.RotateDirection;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;
import ro.tuiasi.ac.common.StartCommand;
import ro.tuiasi.ac.common.StopCommand;

/**
 * Represents a robot that collects sensor data and executes commands.
 *
 * @author Your Name
 */
public class Robot {

    /** The sensor set attached to this robot. */
    private final SensorSet sensorSet;

    /** Flag indicating whether the robot is actively sending data. */
    private boolean isSendingData;

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(Robot.class);

    /**
     * Creates a new robot with the specified sensor set.
     *
     * @param sensorSetParam The sensor configuration for this robot
     */
    public Robot(final SensorSet sensorSetParam) {
        this.sensorSet = sensorSetParam;
        this.isSendingData = false;
    }

    /**
     * Collects current data from all sensors.
     *
     * @return SensorDataSet containing readings from all sensors
     */
    public SensorDataSet collectData() {
        return sensorSet.collectData();
    }

    /**
     * Checks if the robot is currently sending data.
     *
     * @return true if sending data, false otherwise
     */
    public boolean isSending() {
        return this.isSendingData;
    }

    /**
     * Executes a command on the robot.
     *
     * <p>Supported command types:
     * <ul>
     * <li>{@link StartCommand} - Starts data transmission</li>
     * <li>{@link StopCommand} - Stops data transmission</li>
     * <li>{@link MoveCommand} - Moves the camera target position</li>
     * <li>{@link RotateCommand} - Rotates the camera target position</li>
     * </ul>
     *
     * @param cmd The command to execute
     */
    public void executeCommand(final Command cmd) {
        if (cmd instanceof StartCommand) {
            this.isSendingData = true;
            LOG.info("Started sending data.");
        } else if (cmd instanceof StopCommand) {
            this.isSendingData = false;
            LOG.info("Stopped sending data.");
        } else if (cmd instanceof MoveCommand mov) {
            MoveDirection dir = mov.getDirection();
            float dist = mov.getDistance();
            int offsetX = 0;
            int offsetY = 0;
            switch (dir) {
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
            default:
                LOG.info("Move direction unknown.");
            }
            int newTargetX = sensorSet.getCameraSensor().getTargetX();
            newTargetX += offsetX;
            int newTargetY = sensorSet.getCameraSensor().getTargetY();
            newTargetY += offsetY;

            if (newTargetX < 0) {
                newTargetX = 0;
            }
            if (newTargetY < 0) {
                newTargetY = 0;
            }
            if (newTargetX > CameraSensor.WIDTH) {
                newTargetX = CameraSensor.WIDTH;
            }
            if (newTargetY < CameraSensor.HEIGHT) {
                newTargetY = CameraSensor.HEIGHT;
            }

            sensorSet.getCameraSensor().setTargetPosition(
                    newTargetX,
                    newTargetY
                    );
            LOG.info("Move command executed.");
        } else if (cmd instanceof RotateCommand rot) {
            RotateDirection dir = rot.getOrientation();
            float degrees = rot.getGrade();
            switch (dir) {
            case LEFT:
                break;
            case RIGHT:
                degrees = -degrees;
                break;
            default:
                LOG.info("Rotation direction unknown.");
            }
            int newTargetX = sensorSet.getCameraSensor().getTargetX();
            newTargetX += degrees;
            int newTargetY = sensorSet.getCameraSensor().getTargetY();

            if (newTargetX < 0) {
                newTargetX = 0;
            }

            if (newTargetX > CameraSensor.WIDTH) {
                newTargetX = CameraSensor.WIDTH;
            }
            sensorSet.getCameraSensor().setTargetPosition(
                    newTargetX,
                    newTargetY
                    );
            sensorSet.getGyroscopeSensor().update(degrees, degrees);
            LOG.info("Rotate command executed.");
        } else {
            LOG.info("Command unknown");
        }
    }
}
