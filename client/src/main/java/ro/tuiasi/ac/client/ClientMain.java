package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.*;
import ro.tuiasi.ac.common.KafkaProducerUtil;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientMain {
    private static KafkaProducerUtil producer;
    private static KafkaConsumerUtil consumer;
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final AtomicInteger messageCounter = new AtomicInteger(1);
    
    // Store sent messages to calculate latency when response arrives
    private static final ConcurrentHashMap<String, Long> pendingMessages = new ConcurrentHashMap<>();
    
    private static final AtomicInteger totalMessages = new AtomicInteger(0);
    private static long totalRoundTripTime = 0;
    private static long minRoundTripTime = Long.MAX_VALUE;
    private static long maxRoundTripTime = 0;
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Client...");
        String bootstrapServers = Config.getKafkaBootstrapServers();
        
        // Create topics if they don't exist
        KafkaTopicUtil.createTopicsIfNotExist(bootstrapServers, 
            Config.CLIENT_TO_SERVER_TOPIC, 
            Config.SERVER_TO_CLIENT_TOPIC);
        
        // Initialize producer and consumer
        producer = new KafkaProducerUtil(bootstrapServers);
        consumer = new KafkaConsumerUtil(bootstrapServers, "client-group", Config.SERVER_TO_CLIENT_TOPIC);
        
        // Listen for messages from server
        consumer.listen(ServerMessage.class, message -> {
            ServerMessage serverMsg = (ServerMessage) message;
            System.out.println("📥 Client received: " + serverMsg);
            handleServerMessage(serverMsg);
        });
        
        System.out.println("✅ Client ready and listening");
        
        // Start sending periodic messages to server after 3 seconds
        try {
            Thread.sleep(3000);
            startSendingMessages();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Keep client running
        keepAlive();
    }
    
    private static void startSendingMessages() {
        System.out.println("📡 Client starting to send messages every 5 seconds...");
        System.out.println("📊 Latency tracking enabled - will measure round-trip time for each message\n");
        
        scheduler.scheduleAtFixedRate(() -> {
            String content = "Message #" + messageCounter.getAndIncrement() + " from Client";
            ClientMessage message = new ClientMessage(content);
            
            // Store sent time before sending
            long sendTime = System.currentTimeMillis();
            pendingMessages.put(message.getId(), sendTime);
            
            producer.sendMessage(Config.CLIENT_TO_SERVER_TOPIC, message);
            System.out.println("📤 Client sent: " + message);
            System.out.println("   ⏱️  Sent at: " + sendTime);
        }, 0, 5, TimeUnit.SECONDS);
    }
    
    private static void handleServerMessage(ServerMessage message) {
        System.out.println("⚙️ Processing server response: " + message.getContent());
        
        // Check if this is a response to a client message
        if (message.getClientMessageId() != null) {
            Long sentTime = pendingMessages.remove(message.getClientMessageId());
            
            if (sentTime != null) {
                long receivedTime = System.currentTimeMillis();
                long roundTripTime = receivedTime - sentTime;
                
                // Update statistics
                totalMessages.incrementAndGet();
                totalRoundTripTime += roundTripTime;
                minRoundTripTime = Math.min(minRoundTripTime, roundTripTime);
                maxRoundTripTime = Math.max(maxRoundTripTime, roundTripTime);
                
                // Calculate server processing time
                long serverProcessingTime = message.getServerSentTimestamp() - message.getServerReceivedTimestamp();
                long networkTime = roundTripTime - serverProcessingTime;
                
                System.out.println("\n📊 ═══════════════ LATENCY REPORT ═══════════════");
                System.out.printf("   📨 Message ID: %s%n", message.getClientMessageId());
                System.out.printf("   ⏰ Client sent:        %d%n", sentTime);
                System.out.printf("   🖥️  Server received:    %d%n", message.getServerReceivedTimestamp());
                System.out.printf("   🖥️  Server sent:        %d%n", message.getServerSentTimestamp());
                System.out.printf("   📱 Client received:    %d%n", receivedTime);
                System.out.println("   ───────────────────────────────────────────");
                System.out.printf("   ⚙️  Server processing:   %d ms%n", serverProcessingTime);
                System.out.printf("   🌐 Network round-trip:  %d ms%n", networkTime);
                System.out.printf("   🔄 Total round-trip:    %d ms%n", roundTripTime);
                System.out.println("   ═══════════════════════════════════════════\n");
                
                // Optional: Calculate statistics
                printLatencyStats();
                
                // Print running average every 5 messages
                if (totalMessages.get() % 5 == 0) {
                    printRunningStats();
                }
                
            } else {
                System.out.println("⚠️ Received response for unknown message ID: " + message.getClientMessageId());
            }
        } else {
            System.out.println("💬 Server-initiated message (no latency tracking)");
        }
        
        if (message.getContent().contains("received")) {
            System.out.println("✅ Server acknowledged our message!");
        }
        
        if (message.getClientMessageId() != null) {
            Long sentTime = pendingMessages.remove(message.getClientMessageId());

        }
    }
    
    private static void printRunningStats() {
        double avgRtt = totalRoundTripTime / (double) totalMessages.get();
        System.out.println("\n📊 ─────────── RUNNING LATENCY STATS ───────────");
        System.out.printf("   Messages sent: %d%n", totalMessages.get());
        System.out.printf("   Avg RTT: %.2f ms%n", avgRtt);
        System.out.printf("   Min RTT: %d ms%n", minRoundTripTime);
        System.out.printf("   Max RTT: %d ms%n", maxRoundTripTime);
        System.out.println("   ───────────────────────────────────────────\n");
    }
    
    private static void printLatencyStats() {
        // This could be expanded to track min/max/average
        System.out.println("📈 Active pending messages: " + pendingMessages.size());
    }
    
    private static void keepAlive() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}