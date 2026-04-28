// server/src/main/java/ro/tuiasi/ac/server/ServerMain.java
package ro.tuiasi.ac.server;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.tuiasi.ac.common.*;

public class ServerMain {
    private static KafkaProducerUtil producer;
    private static KafkaConsumerUtil consumer;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public static void main(String[] args) {
        System.out.println("Starting server...");
        String bootstrapServers = Config.getKafkaBootstrapServers();
        
        // Producer for sending commands to clients
        producer = new KafkaProducerUtil(bootstrapServers);
        
        // Consumer for receiving sensor data
        consumer = new KafkaConsumerUtil(bootstrapServers, "server-group", Config.SENSOR_DATA_TOPIC);
        System.out.println("Server started.");
        
        // Give consumer time to subscribe
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Start listening for messages
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Message data = consumer.pollData(Message.class, Duration.ofMillis(1000));
                if (data != null) {
                    System.out.println("[SERVER RECEIVED] " + data.getVal());
                    processSensorData(data);
                }
            }
        });
        
        // Wait a bit before sending command
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        sendCommandToClient("Hello there, my client!");
    }
    
    private static void sendCommandToClient(String commandType) {
        Message response = new Message();
        response.setVal(commandType);
        producer.sendData(Config.COMMAND_TOPIC, response);
        System.out.println("Sent command: " + commandType);
    }
    
    private static void processSensorData(Message data) {
        System.out.println("[SERVER PROCESSING] " + data.getVal());
    }
}