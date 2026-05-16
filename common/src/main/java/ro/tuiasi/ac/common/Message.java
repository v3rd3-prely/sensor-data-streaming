package ro.tuiasi.ac.common;

import java.io.Serializable;

/**
 * Simple serializable message object.
 * Used for transferring text data between
 * application components.
 */
public class Message implements Serializable{
	/**
	 * Message text value.
	 */
	private String val;
	
	/**
     * Default constructor.
     */
    public Message() {}
	
	/**
	 * Returns the message value.
	 *
	 * @return message text
	 */
	public String getVal() {
		return this.val;
	}
	
	/**
	 * Sets the message value.
	 *
	 * @param val message text
	 */
	public void setVal(String val) {
		this.val = val;
	}
}
