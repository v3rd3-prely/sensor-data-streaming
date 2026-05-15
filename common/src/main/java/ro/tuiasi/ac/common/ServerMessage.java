package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a message sent from the server to the client.
 * Can be either a direct server message or a response
 * to a specific client message.
 */
public class ServerMessage implements Serializable {
	/**
	 * Message content.
	 */
	private String content;
	/**
	 * Identifier of the client message this message responds to.
	 */
	private String clientMessageId;  // Track which client message this responds to
	/**
	 * Timestamp when the server received the client message.
	 */
	private long serverReceivedTimestamp;
	/**
	 * Timestamp when the server sent the message.
	 */
	private long serverSentTimestamp;
    
	/**
	 * Default constructor required for serialization/deserialization.
	 */
    public ServerMessage() {}
    
    // Constructor for server-initiated messages (no client tracking)
    /**
     * Creates a server-generated message.
     *
     * @param content message content
     */
    public ServerMessage(String content) {
        this.content = content;
        this.serverSentTimestamp = Instant.now().toEpochMilli();
    }
    
    // Constructor for response to client message
    /**
     * Creates a response message for a client message.
     *
     * @param content response content
     * @param clientMessage client message being answered
     */
    public ServerMessage(String content, ClientMessage clientMessage) {
        this.content = content;
        this.clientMessageId = clientMessage.getId();
        this.serverReceivedTimestamp = Instant.now().toEpochMilli();
        this.serverSentTimestamp = this.serverReceivedTimestamp; // Will be updated when actually sent
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
     * Returns the related client message identifier.
     *
     * @return client message id
     */
    public String getClientMessageId() {
        return clientMessageId;
    }
    
    /**
     * Sets the related client message identifier.
     *
     * @param clientMessageId client message id
     */
    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }
    /**
     * Returns the timestamp when the server received the message.
     *
     * @return received timestamp
     */
    public long getServerReceivedTimestamp() {
        return serverReceivedTimestamp;
    }
    
    /**
     * Sets the received timestamp.
     *
     * @param serverReceivedTimestamp received timestamp
     */
    public void setServerReceivedTimestamp(long serverReceivedTimestamp) {
        this.serverReceivedTimestamp = serverReceivedTimestamp;
    }
    
    /**
     * Returns the timestamp when the server sent the message.
     *
     * @return sent timestamp
     */
    public long getServerSentTimestamp() {
        return serverSentTimestamp;
    }
    
    /**
     * Sets the sent timestamp.
     *
     * @param serverSentTimestamp sent timestamp
     */
    public void setServerSentTimestamp(long serverSentTimestamp) {
        this.serverSentTimestamp = serverSentTimestamp;
    }
    
    /**
     * Returns a formatted string representation of the server message.
     *
     * @return formatted message string
     */
    @Override
    public String toString() {
        if (clientMessageId != null) {
            return String.format("[ServerMessage response to %s] %s (received: %d, sent: %d)", 
                clientMessageId, content, serverReceivedTimestamp, serverSentTimestamp);
        } else {
            return String.format("[ServerMessage] %s (sent: %d)", content, serverSentTimestamp);
        }
    }
}