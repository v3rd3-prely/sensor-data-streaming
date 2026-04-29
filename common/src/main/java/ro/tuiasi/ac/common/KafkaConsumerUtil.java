package ro.tuiasi.ac.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class KafkaConsumerUtil {
    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile boolean running = true;
    
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
        System.out.printf("✅ Subscribed to topic: %s (group: %s)%n", topic, groupId);
    }
    
    public void listen(Class<?> messageClass, Consumer<Object> handler) {
        Thread pollingThread = new Thread(() -> {
            while (running) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            Object message = objectMapper.readValue(record.value(), messageClass);
                            System.out.printf("📨 Received from %s [p%d, o%d]%n", 
                                record.topic(), record.partition(), record.offset());
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
    
    public void stop() {
        running = false;
        consumer.wakeup();
    }
    
    public void close() {
        stop();
        consumer.close();
    }
}