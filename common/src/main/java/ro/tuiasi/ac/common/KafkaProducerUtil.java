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
 * Utility class for sending messages to Kafka topics. Serializes Java objects
 * into JSON format and publishes them through a Kafka producer.
 */
public class KafkaProducerUtil {

	/**
	 * Kafka producer used for publishing messages.
	 */
	private final KafkaProducer<String, String> producer;
	/**
	 * JSON object mapper used for message serialization.
	 */
	private final JsonMapper objectMapper;

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(KafkaProducerUtil.class);

	/**
	 * Default constructor.
	 */
	public KafkaProducerUtil() {
		this.producer = null;
		this.objectMapper = null;
	}

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

		props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 5 * 1024 * 1024); // 5 MB
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 32 * 1024 * 1024); // 32 MB

		// Enable compression (reduces size by 70-90%)
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4"); // or "snappy", "gzip", "zstd"

		this.producer = new KafkaProducer<>(props);

		// MODERN BUILDER PATTERN - No deprecated methods!
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType("ro.tuiasi.ac.common").allowIfBaseType(Command.class).build();

		this.objectMapper = JsonMapper.builder()
				// Configure typing for polymorphism
				.activateDefaultTyping(typeValidator, JsonMapper.DefaultTyping.NON_FINAL,
						com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY)
				// Configure features using builder pattern
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				// Optional: Pretty print for debugging (remove in production)
				.configure(SerializationFeature.INDENT_OUTPUT, false).build();
	}

	/**
	 * Serializes and sends a message to a Kafka topic.
	 *
	 * @param topic   Kafka topic name
	 * @param message Java object to serialize and send
	 */
	public void sendMessage(String topic, Object message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);

			producer.send(record, (metadata, exception) -> {
				if (exception == null) {
					if (log.isInfoEnabled())
						log.info("✅ Sent to %s [p%d, o%d]%n", topic, metadata.partition(), metadata.offset());
				} else {
					if (log.isInfoEnabled())
						log.info("❌ Failed to send to %s: %s%n", topic, exception.getMessage());
				}
			});
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error("❌ Serialization error: " + e.getMessage());
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