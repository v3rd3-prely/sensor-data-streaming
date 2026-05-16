package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//Tell Jackson how to handle polymorphic Command interface
@JsonTypeInfo(
 use = JsonTypeInfo.Id.NAME,
 include = JsonTypeInfo.As.PROPERTY,
 property = "commandType"  // Adds a field to identify the concrete type
)
@JsonSubTypes({
 @JsonSubTypes.Type(value = MoveCommand.class, name = "MOVE"),
 @JsonSubTypes.Type(value = RotateCommand.class, name = "ROTATE"),
 @JsonSubTypes.Type(value = StopCommand.class, name = "STOP"),
 @JsonSubTypes.Type(value = StartCommand.class, name = "START")
})
public class ServerMessage implements Serializable {
	
    private Command content;
    private String clientMessageId;  // Track which client message this responds to
    private long serverReceivedTimestamp;
    private long serverSentTimestamp;
    
    public ServerMessage() {}
    
    // Constructor for server-initiated messages (no client tracking)
    public ServerMessage(Command content) {
        this.content = content;
        this.serverSentTimestamp = Instant.now().toEpochMilli();
    }
    
    // Constructor for response to client message
    public ServerMessage(Command content, ClientMessage clientMessage) {
        this.content = content;
        this.clientMessageId = clientMessage.getId();
        this.serverReceivedTimestamp = Instant.now().toEpochMilli();
        this.serverSentTimestamp = this.serverReceivedTimestamp; // Will be updated when actually sent
    }
    
    public Command getContent() {
        return content;
    }
    
    public void setContent(Command content) {
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