package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Config}.
 * Verifies that Kafka topic names are correctly exposed and that bootstrap
 * server configuration returns a non-empty value.
 *
 * @author Your Name
 */
public final class ConfigTest {

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private ConfigTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that Kafka topic name constants are correctly defined.
     * Verifies that client-to-server and server-to-client topic names
     * match the expected values.
     *
     * @see Config#CLIENT_TO_SERVER_TOPIC
     * @see Config#SERVER_TO_CLIENT_TOPIC
     */
    @Test
    void shouldExposeKafkaTopicNames() {
        assertEquals("client-to-server", Config.CLIENT_TO_SERVER_TOPIC);
        assertEquals("server-to-client", Config.SERVER_TO_CLIENT_TOPIC);
    }

    /**
     * Tests that Kafka bootstrap servers configuration returns a valid value.
     * Verifies that the returned string is not null and not blank,
     * regardless of whether it comes from environment variable or default.
     *
     * @see Config#getKafkaBootstrapServers()
     */
    @Test
    void shouldReturnKafkaBootstrapServers() {
        String bootstrapServers = Config.getKafkaBootstrapServers();

        assertNotNull(bootstrapServers);
        assertFalse(bootstrapServers.isBlank());
    }
}
