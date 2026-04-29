package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;

public class ServerMessage implements Serializable {
    private String content;
    private String clientMessageId;  // Track which client message this responds to
    private long serverReceivedTimestamp;
    private long serverSentTimestamp;
    
    public ServerMessage() {}
    
    // Constructor for server-initiated messages (no client tracking)
    public ServerMessage(String content) {
        this.content = content;
        this.serverSentTimestamp = Instant.now().toEpochMilli();
    }
    
    // Constructor for response to client message
    public ServerMessage(String content, ClientMessage clientMessage) {
        this.content = content;
        this.clientMessageId = clientMessage.getId();
        this.serverReceivedTimestamp = Instant.now().toEpochMilli();
        this.serverSentTimestamp = this.serverReceivedTimestamp; // Will be updated when actually sent
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getClientMessageId() {
        return clientMessageId;
    }
    
    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }
    
    public long getServerReceivedTimestamp() {
        return serverReceivedTimestamp;
    }
    
    public void setServerReceivedTimestamp(long serverReceivedTimestamp) {
        this.serverReceivedTimestamp = serverReceivedTimestamp;
    }
    
    public long getServerSentTimestamp() {
        return serverSentTimestamp;
    }
    
    public void setServerSentTimestamp(long serverSentTimestamp) {
        this.serverSentTimestamp = serverSentTimestamp;
    }
    
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