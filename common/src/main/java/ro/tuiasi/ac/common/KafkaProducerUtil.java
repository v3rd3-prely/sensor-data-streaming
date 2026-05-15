package ro.tuiasi.ac.common;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;

/**
 * Utility class for sending messages to Kafka topics.
 * Serializes Java objects into JSON format
 * and publishes them through a Kafka producer.
 */
public class KafkaProducerUtil {
	/**
	 * Kafka producer used for publishing messages.
	 */
    private final KafkaProducer<String, String> producer;
    /**
     * JSON object mapper used for message serialization.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Default constructor.
     */
    public KafkaProducerUtil() {
		this.producer = null;}
    
    /**
     * Creates a Kafka producer utility.
     *
     * @param bootstrapServers Kafka bootstrap servers address
     */
    public KafkaProducerUtil(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        this.producer = new KafkaProducer<>(props);
    }
    /**
     * Serializes and sends a message to a Kafka topic.
     *
     * @param topic Kafka topic name
     * @param message Java object to serialize and send
     */
    public void sendMessage(String topic, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
            
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.printf("✅ Sent to %s [p%d, o%d]%n", 
                        topic, metadata.partition(), metadata.offset());
                } else {
                    System.err.printf("❌ Failed to send to %s: %s%n", topic, exception.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ Serialization error: " + e.getMessage());
        }
    }
    /**
     * Flushes pending messages and closes the Kafka producer.
     */
    public void close() {
        producer.flush();
        producer.close();
    }
}