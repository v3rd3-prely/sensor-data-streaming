package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Command to start the robot's data transmission.
 *
 * @author Your Name
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class StartCommand implements Command {

    /**
     * Default constructor required for Jackson deserialization.
     */
    public StartCommand() {
    }

    @Override
    public CommandType getType() {
        return CommandType.START;
    }

    @Override
    public String toString() {
        return "StartCommand{}";
    }
}
