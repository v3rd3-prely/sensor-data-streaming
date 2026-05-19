package ro.tuiasi.ac.common;

/**
 * Central configuration class for the application.
 * Stores Kafka topics and Kafka connection settings.
 */
public final class Config {

    /** Kafka topic used for client-to-server communication. */
    public static final String CLIENT_TO_SERVER_TOPIC = "client-to-server";

    /** Kafka topic used for server-to-client communication. */
    public static final String SERVER_TO_CLIENT_TOPIC = "server-to-client";

    /** Environment variable name for Kafka bootstrap servers. */
    private static final String KAFKA_BOOTSTRAP_SERVERS_ENV =
            "KAFKA_BOOTSTRAP_SERVERS";

    /** Default Kafka bootstrap server address. */
    private static final String DEFAULT_KAFKA_BOOTSTRAP = "kafka:9092";

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private Config() {
        // Utility class - no instantiation needed
    }

    /**
     * Returns the Kafka bootstrap servers address.
     * Uses the environment variable if available,
     * otherwise returns the default Kafka address.
     *
     * @return Kafka bootstrap servers address
     */
    public static String getKafkaBootstrapServers() {
        String envServers = System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV);
        return envServers != null ? envServers : DEFAULT_KAFKA_BOOTSTRAP;
    }
}
