package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class ClientMessage implements Serializable {
    private String id;
    private String content;
    private long sentTimestamp;
    
    public ClientMessage() {}
    
    public ClientMessage(String content) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.sentTimestamp = Instant.now().toEpochMilli();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public long getSentTimestamp() {
        return sentTimestamp;
    }
    
    public void setSentTimestamp(long sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }
    
    @Override
    public String toString() {
        return String.format("[ClientMessage id=%s] %s (sent: %d)", id, content, sentTimestamp);
    }
}