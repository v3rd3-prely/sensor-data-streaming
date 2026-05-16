package ro.tuiasi.ac.common;

/**
 * Enumeration of available command types for robot control.
 * 
 * @author Your Name
 */
public enum CommandType {
	/** Move command - changes robot position */
	MOVE,

	/** Rotate command - changes robot orientation */
	ROTATE,

	/** Stop command - halts all robot sendings */
	STOP,

	/** Start command - resumes robot sendings */
	START
}