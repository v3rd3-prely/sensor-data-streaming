package ro.tuiasi.ac.common;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.Properties;

public class KafkaProducerUtil {
	private final KafkaProducer<String, String> producer;
	private final JsonMapper objectMapper;

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

	public void sendMessage(String topic, Object message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);

			producer.send(record, (metadata, exception) -> {
				if (exception == null) {
					System.out.printf("✅ Sent to %s [p%d, o%d]%n", topic, metadata.partition(), metadata.offset());
				} else {
					System.err.printf("❌ Failed to send to %s: %s%n", topic, exception.getMessage());
				}
			});
		} catch (Exception e) {
			System.err.println("❌ Serialization error: " + e.getMessage());
		}
	}

	public void close() {
		producer.flush();
		producer.close();
	}
}