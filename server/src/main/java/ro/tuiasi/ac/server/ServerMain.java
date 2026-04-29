package ro.tuiasi.ac.server;

import ro.tuiasi.ac.common.*;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain {
    private static KafkaProducerUtil producer;
    private static KafkaConsumerUtil consumer;
    
    // Track server-side metrics
    private static final AtomicInteger messagesProcessed = new AtomicInteger(0);
    private static long totalProcessingTime = 0;
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Server...");
        String bootstrapServers = Config.getKafkaBootstrapServers();
        
        // Create topics if they don't exist
        KafkaTopicUtil.createTopicsIfNotExist(bootstrapServers, 
            Config.CLIENT_TO_SERVER_TOPIC, 
            Config.SERVER_TO_CLIENT_TOPIC);
        
        // Initialize producer and consumer
        producer = new KafkaProducerUtil(bootstrapServers);
        consumer = new KafkaConsumerUtil(bootstrapServers, "server-group", Config.CLIENT_TO_SERVER_TOPIC);
        
        // Listen for messages from clients
        consumer.listen(ClientMessage.class, message -> {
            ClientMessage clientMsg = (ClientMessage) message;
            long receiveTime = Instant.now().toEpochMilli();
            System.out.println("📥 Server received: " + clientMsg);
            System.out.println("   ⏱️  Received at: " + receiveTime);
            processClientMessage(clientMsg, receiveTime);
        });
        
        System.out.println("✅ Server ready and listening");
        
        // Send a test message to client after 5 seconds
        try {
            Thread.sleep(5000);
            sendMessageToClient("Hello from Server!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Keep server running
        keepAlive();
    }
    
    private static void sendMessageToClient(String content) {
        ServerMessage message = new ServerMessage(content);
        message.setServerSentTimestamp(Instant.now().toEpochMilli());
        producer.sendMessage(Config.SERVER_TO_CLIENT_TOPIC, message);
        System.out.println("📤 Server sent: " + message);
    }
    
    private static void processClientMessage(ClientMessage message, long receiveTime) {
        long startProcessing = System.currentTimeMillis();
        System.out.println("⚙️ Processing: " + message.getContent());
        
        // Simulate some processing work (you can adjust or remove this)
        try {
            Thread.sleep(10); // Simulate 10ms of processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long processingTime = System.currentTimeMillis() - startProcessing;
        
        // Create response message with timing information
        ServerMessage response = new ServerMessage("Server received: " + message.getContent(), message);
        response.setServerReceivedTimestamp(receiveTime);
        response.setServerSentTimestamp(Instant.now().toEpochMilli());
        
        // Update server metrics
        int processed = messagesProcessed.incrementAndGet();
        totalProcessingTime += processingTime;
        double avgProcessingTime = totalProcessingTime / (double) processed;
        
        System.out.printf("   ⚙️  Processing took: %d ms (avg: %.2f ms)%n", processingTime, avgProcessingTime);
        
        // Send response back to client
        producer.sendMessage(Config.SERVER_TO_CLIENT_TOPIC, response);
        System.out.println("📤 Server sent response: " + response);
        System.out.println("   ⏱️  Sent at: " + response.getServerSentTimestamp());
    }
    
    private static void keepAlive() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}