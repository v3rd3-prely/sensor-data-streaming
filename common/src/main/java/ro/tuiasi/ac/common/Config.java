package ro.tuiasi.ac.common;

public class Config {
    // Kafka topics
    public static final String CLIENT_TO_SERVER_TOPIC = "client-to-server";
    public static final String SERVER_TO_CLIENT_TOPIC = "server-to-client";
    
    // Kafka bootstrap servers
    private static final String KAFKA_BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";
    private static final String DEFAULT_KAFKA_BOOTSTRAP = "kafka:9092";
    
    public static String getKafkaBootstrapServers() {
        String envServers = System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV);
        return envServers != null ? envServers : DEFAULT_KAFKA_BOOTSTRAP;
    }
}