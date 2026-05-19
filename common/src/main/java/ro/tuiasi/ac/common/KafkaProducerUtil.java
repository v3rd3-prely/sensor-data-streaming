package ro.tuiasi.ac.common;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.Properties;

/**
 * Utility class for sending messages to Kafka topics.
 * Serializes Java objects into JSON format and publishes them
 * through a Kafka producer.
 */
public class KafkaProducerUtil {

    /** Number of retries for failed sends. */
    private static final int RETRIES_COUNT = 3;

    /** Maximum request size in bytes (5 MB). */
    private static final int MAX_REQUEST_SIZE_BYTES = 5 * 1024 * 1024;

    /** Buffer memory size in bytes (32 MB). */
    private static final int BUFFER_MEMORY_BYTES = 32 * 1024 * 1024;

    /** One kilobyte in bytes. */
    private static final int KB = 1024;

    /** One megabyte in bytes (KB * KB). */
    private static final int MB = KB * KB;

    /** Maximum request size (5 MB). */
    private static final int MAX_REQUEST_SIZE = 5 * MB;

    /** Buffer memory size (32 MB). */
    private static final int BUFFER_MEMORY = 32 * MB;

    /** Kafka producer used for publishing messages. */
    private final KafkaProducer<String, String> producer;

    /** JSON object mapper used for message serialization. */
    private final JsonMapper objectMapper;

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(
            KafkaProducerUtil.class);

    /**
     * Default constructor (not recommended - creates unusable instance).
     *
     * @deprecated Use {@link #KafkaProducerUtil(String)} instead
     */
    @Deprecated
    public KafkaProducerUtil() {
        this.producer = null;
        this.objectMapper = null;
    }

    /**
     * Creates a Kafka producer utility.
     *
     * @param bootstrapServers Kafka bootstrap servers address
     */
    public KafkaProducerUtil(final String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, RETRIES_COUNT);

        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, MAX_REQUEST_SIZE);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY);

        // Enable compression (reduces size by 70-90%)
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        this.producer = new KafkaProducer<>(props);

        // Configure polymorphic type validation
        PolymorphicTypeValidator typeValidator =
                BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("ro.tuiasi.ac.common")
                .allowIfBaseType(Command.class)
                .build();

        this.objectMapper = JsonMapper.builder()
                .activateDefaultTyping(typeValidator,
                        JsonMapper.DefaultTyping.NON_FINAL,
                        com.fasterxml.jackson.
                        annotation.JsonTypeInfo.As.PROPERTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .build();
    }

    /**
     * Serializes and sends a message to a Kafka topic.
     *
     * @param topic Kafka topic name
     * @param message Java object to serialize and send
     */
    public void sendMessage(final String topic, final Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, json);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("✅ Sent to {} [p{}, o{}]",
                                topic, metadata.partition(),
                                metadata.offset());
                    }
                } else {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("❌ Failed to send to {}: {}",
                                topic, exception.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("❌ Serialization error: {}", e.getMessage());
            }
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
