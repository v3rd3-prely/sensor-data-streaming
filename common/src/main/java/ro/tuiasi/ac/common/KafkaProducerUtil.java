package ro.tuiasi.ac.common;
//common/src/main/java/ro/tuiasi/ac/common/KafkaProducerUtil.java
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducerUtil {
 private final KafkaProducer<String, String> producer;
 private final ObjectMapper objectMapper = new ObjectMapper();
 
 public KafkaProducerUtil(String bootstrapServers) {
     Properties props = new Properties();
     props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
     
     // Ensure messages are sent immediately
     props.put(ProducerConfig.LINGER_MS_CONFIG, 0);  // Don't batch
     props.put(ProducerConfig.ACKS_CONFIG, "all");   // Wait for confirmation
     
     this.producer = new KafkaProducer<>(props);
 }
 
 public void sendData(String topic, Object data) {
     try {
         String json = objectMapper.writeValueAsString(data);
         ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
         
         // Send synchronously to ensure it's actually sent
         Future<?> future = producer.send(record);
         future.get();  // Wait for send to complete
         System.out.println("Successfully sent message to topic: " + topic);
         
     } catch (Exception e) {
         System.err.println("Error sending message: " + e.getMessage());
         e.printStackTrace();
     }
 }
 
 public void close() {
     producer.flush();  // Force send any buffered messages
     producer.close();
 }
}
