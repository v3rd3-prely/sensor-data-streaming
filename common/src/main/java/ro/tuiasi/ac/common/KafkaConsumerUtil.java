package ro.tuiasi.ac.common;
//common/src/main/java/ro/tuiasi/ac/common/KafkaConsumerUtil.java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerUtil {
 private final KafkaConsumer<String, String> consumer;
 private final ObjectMapper objectMapper = new ObjectMapper();
 
 public KafkaConsumerUtil(String bootstrapServers, String groupId, String topic) {
     Properties props = new Properties();
     props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
     props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
     props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
     props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
     
     // CRITICAL: Where to start reading from
     props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // ← Read all messages
     // Alternative: "latest" (only new messages), "none" (fail if no offset)
     
     // Auto-commit offsets (or commit manually)
     props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
     props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
     
     this.consumer = new KafkaConsumer<>(props);
     consumer.subscribe(List.of(topic));
     
     // Give consumer time to subscribe
     try {
         Thread.sleep(1000);
     } catch (InterruptedException e) {
         e.printStackTrace();
     }
 }
 
 public <T> T pollData(Class<T> clazz, Duration timeout) {
     ConsumerRecords<String, String> records = consumer.poll(timeout);
     for (ConsumerRecord<String, String> record : records) {
         try {
             System.out.println("Received message from partition " + record.partition() + 
                                ", offset " + record.offset());
             return objectMapper.readValue(record.value(), clazz);
         } catch (Exception e) {
             System.err.println("Error deserializing: " + e.getMessage());
             e.printStackTrace();
         }
     }
     return null;
 }
 
 public void close() {
     consumer.close();
 }
}