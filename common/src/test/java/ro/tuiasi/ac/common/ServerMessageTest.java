package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ServerMessage}.
 * Verifies message creation, client message tracking, field accessors,
 * and string representation functionality.
 *
 * @author Your Name
 */
public final class ServerMessageTest {

    /** Test distance value for move command. */
    private static final float TEST_MOVE_DISTANCE = 25.0f;

    /** Test received timestamp value. */
    private static final long TEST_RECEIVED_TIMESTAMP = 100L;

    /** Test sent timestamp value. */
    private static final long TEST_SENT_TIMESTAMP = 200L;

    /** Test client message ID. */
    private static final String TEST_CLIENT_MSG_ID = "client-message-id";

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private ServerMessageTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that the constructor with
     * only a Command properly initializes fields.
     * Verifies that content is stored,
     * clientMessageId is null, and sent timestamp
     * is set to a positive value.
     *
     * @see ServerMessage#ServerMessage(Command)
     */
    @Test
    void constructorWithCommandShouldInitializeFields() {
        Command command = new StopCommand();

        ServerMessage message = new ServerMessage(command);

        assertEquals(command, message.getContent());
        assertNull(message.getClientMessageId());
        assertTrue(message.getServerSentTimestamp() > 0);
    }

    /**
     * Tests that the constructor with ClientMessage properly tracks the
     * client message ID. Verifies that content is stored, clientMessageId
     * is copied from the client message, and both received and sent
     * timestamps are set to positive values.
     *
     * @see ServerMessage#ServerMessage(Command, ClientMessage)
     */
    @Test
    void constructorWithClientMessageShouldTrackClientMessageId() {
        ClientMessage clientMessage = new ClientMessage(
                CommonTestData.sensorDataSet());
        Command command = new StartCommand();

        ServerMessage message = new ServerMessage(command, clientMessage);

        assertEquals(command, message.getContent());
        assertEquals(clientMessage.getId(), message.getClientMessageId());
        assertTrue(message.getServerReceivedTimestamp() > 0);
        assertTrue(message.getServerSentTimestamp() > 0);
    }

    /**
     * Tests that all setters and getters work correctly.
     * Verifies that each field can
     * be set and retrieved with the expected value.
     *
     * @see ServerMessage#setContent(Command)
     * @see ServerMessage#setClientMessageId(String)
     * @see ServerMessage#setServerReceivedTimestamp(long)
     * @see ServerMessage#setServerSentTimestamp(long)
     */
    @Test
    void settersAndGettersShouldWork() {
        ServerMessage message = new ServerMessage();
        Command command = new MoveCommand(
                MoveDirection.FRONT, TEST_MOVE_DISTANCE);

        message.setContent(command);
        message.setClientMessageId(TEST_CLIENT_MSG_ID);
        message.setServerReceivedTimestamp(TEST_RECEIVED_TIMESTAMP);
        message.setServerSentTimestamp(TEST_SENT_TIMESTAMP);

        assertEquals(command, message.getContent());
        assertEquals(TEST_CLIENT_MSG_ID, message.getClientMessageId());
        assertEquals(TEST_RECEIVED_TIMESTAMP,
                message.getServerReceivedTimestamp());
        assertEquals(TEST_SENT_TIMESTAMP,
                message.getServerSentTimestamp());
    }

    /**
     * Tests that the string representation contains key message information.
     * Verifies that toString includes both the message type and command
     * information.
     *
     * @see ServerMessage#toString()
     */
    @Test
    void toStringShouldContainCommandInformation() {
        ServerMessage message = new ServerMessage(new StopCommand());

        String result = message.toString();

        assertTrue(result.contains("ServerMessage"));
        assertTrue(result.contains("StopCommand"));
    }
}
