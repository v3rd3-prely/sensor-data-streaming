package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Command to stop the robot's data transmission.
 *
 * @author Your Name
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class StopCommand implements Command {

    /**
     * Default constructor required for Jackson deserialization.
     */
    public StopCommand() {
    }

    @Override
    public CommandType getType() {
        return CommandType.STOP;
    }

    @Override
    public String toString() {
        return "StopCommand{}";
    }
}
