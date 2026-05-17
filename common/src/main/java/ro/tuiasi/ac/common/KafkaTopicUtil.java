package ro.tuiasi.ac.common;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Utility class for creating Kafka topics. Ensures that required Kafka topics
 * exist before the application starts communication.
 */
public class KafkaTopicUtil {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(KafkaTopicUtil.class);

	/**
	 * Default constructor.
	 */
	public KafkaTopicUtil() {
	}

	/**
	 * Creates Kafka topics if they do not already exist.
	 *
	 * @param bootstrapServers Kafka bootstrap servers address
	 * @param topics           Kafka topic names to create
	 */
	public static void createTopicsIfNotExist(String bootstrapServers, String... topics) {
		Properties props = new Properties();
		props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

		try (AdminClient admin = AdminClient.create(props)) {
			List<NewTopic> newTopics = Arrays.stream(topics).map(topic -> new NewTopic(topic, 1, (short) 1)).toList();

			admin.createTopics(newTopics).all().get();
			if (log.isInfoEnabled())
				log.info("✅ Created topics: %s%n", Arrays.toString(topics));
		} catch (ExecutionException e) {
			if (e.getCause() instanceof TopicExistsException) {
				if (log.isInfoEnabled())
					log.info("ℹ️ Topics already exist: %s%n", Arrays.toString(topics));
			} else {
				if (log.isErrorEnabled())
					log.error("❌ Failed to create topics: " + e.getMessage());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("❌ Topic creation interrupted");
		}
	}
}