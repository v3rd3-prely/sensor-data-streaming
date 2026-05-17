package ro.tuiasi.ac.common;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a message sent from the server to the client.
 * Can be either a direct server message or a response to a specific client
 * message.
 *
 * @author Your Name
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "commandType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MoveCommand.class, name = "MOVE"),
    @JsonSubTypes.Type(value = RotateCommand.class, name = "ROTATE"),
    @JsonSubTypes.Type(value = StopCommand.class, name = "STOP"),
    @JsonSubTypes.Type(value = StartCommand.class, name = "START")
})
public final class ServerMessage implements Serializable {

    /** Message content (a command to execute). */
    private Command content;

    /** Identifier of the client message this message responds to. */
    private String clientMessageId;

    /** Timestamp when the server received the client message. */
    private long serverReceivedTimestamp;

    /** Timestamp when the server sent the message. */
    private long serverSentTimestamp;

    /**
     * Default constructor required for serialization/deserialization.
     */
    public ServerMessage() {
    }

    /**
     * Creates a server-generated message (no client tracking).
     *
     * @param contentParam message content
     */
    public ServerMessage(final Command contentParam) {
        this.content = contentParam;
        this.serverSentTimestamp = Instant.now().toEpochMilli();
    }

    /**
     * Creates a response message for a client message.
     *
     * @param contentParam response content
     * @param clientMessage client message being answered
     */
    public ServerMessage(final Command contentParam,
            final ClientMessage clientMessage) {
        this.content = contentParam;
        this.clientMessageId = clientMessage.getId();
        this.serverReceivedTimestamp = Instant.now().toEpochMilli();
        this.serverSentTimestamp = this.serverReceivedTimestamp;
    }

    /**
     * Returns the message content.
     *
     * @return message content
     */
    public Command getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param contentParam message content
     */
    public void setContent(final Command contentParam) {
        this.content = contentParam;
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
     * @param clientMessageIdParam client message id
     */
    public void setClientMessageId(final String clientMessageIdParam) {
        this.clientMessageId = clientMessageIdParam;
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
     * @param serverReceivedTimestampParam received timestamp
     */
    public void setServerReceivedTimestamp(
            final long serverReceivedTimestampParam) {
        this.serverReceivedTimestamp = serverReceivedTimestampParam;
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
     * @param serverSentTimestampParam sent timestamp
     */
    public void setServerSentTimestamp(
            final long serverSentTimestampParam) {
        this.serverSentTimestamp = serverSentTimestampParam;
    }

    @Override
    public String toString() {
        if (clientMessageId != null) {
            return String.format(
                    "[ServerMessage response to %s] %s "
                    + "(received: %d, sent: %d)",
                    clientMessageId, content,
                    serverReceivedTimestamp, serverSentTimestamp);
        } else {
            return String.format(
                    "[ServerMessage] %s (sent: %d)",
                    content, serverSentTimestamp);
        }
    }
}
