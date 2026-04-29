package ro.tuiasi.ac.common;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicUtil {
    
    public static void createTopicsIfNotExist(String bootstrapServers, String... topics) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        try (AdminClient admin = AdminClient.create(props)) {
            List<NewTopic> newTopics = Arrays.stream(topics)
                .map(topic -> new NewTopic(topic, 1, (short) 1))
                .toList();
            
            admin.createTopics(newTopics).all().get();
            System.out.printf("✅ Created topics: %s%n", Arrays.toString(topics));
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                System.out.printf("ℹ️ Topics already exist: %s%n", Arrays.toString(topics));
            } else {
                System.err.println("❌ Failed to create topics: " + e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Topic creation interrupted");
        }
    }
}