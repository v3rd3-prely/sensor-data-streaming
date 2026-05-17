package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    @Test
    void shouldExposeKafkaTopicNames() {
        assertEquals("client-to-server", Config.CLIENT_TO_SERVER_TOPIC);
        assertEquals("server-to-client", Config.SERVER_TO_CLIENT_TOPIC);
    }

    @Test
    void shouldReturnKafkaBootstrapServers() {
        String bootstrapServers = Config.getKafkaBootstrapServers();

        assertNotNull(bootstrapServers);
        assertFalse(bootstrapServers.isBlank());
    }
}