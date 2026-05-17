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

    /** Unique identifier of the message. */
    private String id;

    /** Content of the client message. */
    private SensorDataSet content;

    /** Timestamp when the message was created and sent. */
    private long sentTimestamp;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public ClientMessage() {
    }

    /**
     * Creates a new client message.
     * Generates a unique identifier and timestamp automatically.
     *
     * @param contentParam message content
     */
    public ClientMessage(final SensorDataSet contentParam) {
        this.id = UUID.randomUUID().toString();
        this.content = contentParam;
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
     * @param idParam message id
     */
    public void setId(final String idParam) {
        this.id = idParam;
    }

    /**
     * Returns the message content.
     *
     * @return message content
     */
    public SensorDataSet getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param contentParam message content
     */
    public void setContent(final SensorDataSet contentParam) {
        this.content = contentParam;
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
     * @param sentTimestampParam timestamp in milliseconds
     */
    public void setSentTimestamp(final long sentTimestampParam) {
        this.sentTimestamp = sentTimestampParam;
    }

    /**
     * Returns a string representation of the message.
     *
     * @return formatted message string
     */
    @Override
    public String toString() {
        return String.format(
                "[ClientMessage id=%s] %s (sent: %d)",
                id, content, sentTimestamp);
    }
}
