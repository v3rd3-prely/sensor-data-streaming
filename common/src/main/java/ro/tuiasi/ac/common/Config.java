package ro.tuiasi.ac.common;

public class Config {
    // Kafka topics
    public static final String SENSOR_DATA_TOPIC = "sensor-data";
    public static final String COMMAND_TOPIC = "command";
    
    // Kafka bootstrap servers (will be overridden by environment variables)
    public static final String KAFKA_BOOTSTRAP_SERVERS_ENV = "KAFKA_BOOTSTRAP_SERVERS";
    public static final String DEFAULT_KAFKA_BOOTSTRAP = "kafka:9092";
    
    public static String getKafkaBootstrapServers() {
        String envServers = System.getenv(KAFKA_BOOTSTRAP_SERVERS_ENV);
        return envServers != null ? envServers : DEFAULT_KAFKA_BOOTSTRAP;
    }
}