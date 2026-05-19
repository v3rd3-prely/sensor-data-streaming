package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerMessageTest {

    @Test
    void constructorWithCommandShouldInitializeFields() {
        Command command = new StopCommand();

        ServerMessage message = new ServerMessage(command);

        assertEquals(command, message.getContent());
        assertNull(message.getClientMessageId());
        assertTrue(message.getServerSentTimestamp() > 0);
    }

    @Test
    void constructorWithClientMessageShouldTrackClientMessageId() {
        ClientMessage clientMessage = new ClientMessage(CommonTestData.sensorDataSet());
        Command command = new StartCommand();

        ServerMessage message = new ServerMessage(command, clientMessage);

        assertEquals(command, message.getContent());
        assertEquals(clientMessage.getId(), message.getClientMessageId());
        assertTrue(message.getServerReceivedTimestamp() > 0);
        assertTrue(message.getServerSentTimestamp() > 0);
    }

    @Test
    void settersAndGettersShouldWork() {
        ServerMessage message = new ServerMessage();
        Command command = new MoveCommand(MoveDirection.FRONT, 25.0f);

        message.setContent(command);
        message.setClientMessageId("client-message-id");
        message.setServerReceivedTimestamp(100L);
        message.setServerSentTimestamp(200L);

        assertEquals(command, message.getContent());
        assertEquals("client-message-id", message.getClientMessageId());
        assertEquals(100L, message.getServerReceivedTimestamp());
        assertEquals(200L, message.getServerSentTimestamp());
    }

    @Test
    void toStringShouldContainCommandInformation() {
        ServerMessage message = new ServerMessage(new StopCommand());

        String result = message.toString();

        assertTrue(result.contains("ServerMessage"));
        assertTrue(result.contains("StopCommand"));
    }
}