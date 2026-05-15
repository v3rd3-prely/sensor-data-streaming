package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


/**
 * Represents a message sent from the client application.
 * Contains a unique identifier, message content,
 * and the timestamp when the message was created.
 */

public class ClientMessage implements Serializable {
	/**
	 * Unique identifier of the message.
	 */
    private String id;
    /**
     * Content of the client message.
     */
    private String content;
    /**
     * Timestamp when the message was created and sent.
     */
    private long sentTimestamp;
    
    /**
     * Default constructor required for serialization/deserialization.
     */
    public ClientMessage() {}
    
    /**
     * Creates a new client message.
     * Generates a unique identifier and timestamp automatically.
     *
     * @param content message content
     */
    public ClientMessage(String content) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.sentTimestamp = Instant.now().toEpochMilli();
    }
    
    /**
     * Returns the message identifier.
     *
     * @return message id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the message identifier.
     *
     * @param id message id
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Returns the message content.
     *
     * @return message content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Sets the message content.
     *
     * @param content message content
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Returns the timestamp when the message was sent.
     *
     * @return sent timestamp in milliseconds
     */
    public long getSentTimestamp() {
        return sentTimestamp;
    }
    
    /**
     * Sets the sent timestamp.
     *
     * @param sentTimestamp timestamp in milliseconds
     */
    public void setSentTimestamp(long sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }
    
    /**
     * Returns a string representation of the message.
     *
     * @return formatted message string
     */
    @Override
    public String toString() {
        return String.format("[ClientMessage id=%s] %s (sent: %d)", id, content, sentTimestamp);
    }
}