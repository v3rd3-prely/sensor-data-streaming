package ro.tuiasi.ac.common;

/**
 * Represents a command that can be executed by the system.
 * 
 * @author Your Name
 */
public interface Command {

	/**
	 * Returns the type of this command.
	 * 
	 * @return The command type (e.g., MOVE, ROTATE, START, STOP)
	 */
	CommandType getType();
}