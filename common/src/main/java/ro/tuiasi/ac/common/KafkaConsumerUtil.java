package ro.tuiasi.ac.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Utility class for consuming Kafka messages. Subscribes to a Kafka topic,
 * listens for incoming JSON messages, deserializes them into Java objects, and
 * passes them to a handler.
 */
public class KafkaConsumerUtil {
	/**
	 * Kafka consumer used for reading messages from topics.
	 */
	private final KafkaConsumer<String, String> consumer;

	/**
	 * JSON object mapper used for message deserialization.
	 */
	private final JsonMapper objectMapper;

	/**
	 * Controls the polling loop execution.
	 */
	private volatile boolean running = true;

	/**
	 * Default constructor.
	 */
	public KafkaConsumerUtil() {
		this.consumer = null;
		this.objectMapper = null;
	}

	/**
	 * Creates a Kafka consumer utility and subscribes it to a topic.
	 *
	 * @param bootstrapServers Kafka bootstrap servers address
	 * @param groupId          Kafka consumer group identifier
	 * @param topic            Kafka topic to subscribe to
	 */
	public KafkaConsumerUtil(String bootstrapServers, String groupId, String topic) {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

		this.consumer = new KafkaConsumer<>(props);
		consumer.subscribe(List.of(topic));

		// MODERN BUILDER PATTERN - Same as producer
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType("ro.tuiasi.ac.common").allowIfBaseType(Command.class).build();

		this.objectMapper = JsonMapper.builder()
				.activateDefaultTyping(typeValidator, JsonMapper.DefaultTyping.NON_FINAL,
						com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false).build();

		System.out.printf("✅ Subscribed to topic: %s (group: %s)%n", topic, groupId);
	}

	/**
	 * Starts listening for Kafka messages in a background thread. Each received
	 * message is deserialized into the specified class and passed to the provided
	 * handler.
	 *
	 * @param messageClass class used for JSON deserialization
	 * @param handler      function that processes the deserialized message
	 */
	public void listen(Class<?> messageClass, Consumer<Object> handler) {
		Thread pollingThread = new Thread(() -> {
			while (running) {
				try {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
					for (ConsumerRecord<String, String> record : records) {
						try {
							Object message = objectMapper.readValue(record.value(), messageClass);
							System.out.printf("📨 Received from %s [p%d, o%d]%n", record.topic(), record.partition(),
									record.offset());
							handler.accept(message);
						} catch (Exception e) {
							System.err.println("❌ Deserialization error: " + e.getMessage());
						}
					}
				} catch (Exception e) {
					if (running) {
						System.err.println("❌ Polling error: " + e.getMessage());
					}
				}
			}
		});
		pollingThread.setDaemon(true);
		pollingThread.start();
	}

	/**
	 * Stops the Kafka polling loop.
	 */

	public void stop() {
		running = false;
		consumer.wakeup();
	}

	/**
	 * Stops the consumer and closes the Kafka connection.
	 */
	public void close() {
		stop();
		consumer.close();
	}
}