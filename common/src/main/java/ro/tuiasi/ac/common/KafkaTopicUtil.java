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
 * Utility class for creating Kafka topics.
 * Ensures that required Kafka topics exist before the application starts
 * communication.
 */
public final class KafkaTopicUtil {

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(
            KafkaTopicUtil.class);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private KafkaTopicUtil() {
        // Utility class - no instantiation needed
    }

    /**
     * Creates Kafka topics if they do not already exist.
     *
     * @param bootstrapServers Kafka bootstrap servers address
     * @param topics Kafka topic names to create
     */
    public static void createTopicsIfNotExist(final String bootstrapServers,
            final String... topics) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        try (AdminClient admin = AdminClient.create(props)) {
            List<NewTopic> newTopics = Arrays.stream(topics)
                    .map(topic -> new NewTopic(topic, 1, (short) 1))
                    .toList();

            admin.createTopics(newTopics).all().get();
            if (LOG.isInfoEnabled()) {
                LOG.info("✅ Created topics: {}", Arrays.toString(topics));
            }
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("ℹ️ Topics already exist: {}",
                            Arrays.toString(topics));
                }
            } else {
                if (LOG.isErrorEnabled()) {
                    LOG.error("❌ Failed to create topics: {}",
                            e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("❌ Topic creation interrupted");
        }
    }
}
