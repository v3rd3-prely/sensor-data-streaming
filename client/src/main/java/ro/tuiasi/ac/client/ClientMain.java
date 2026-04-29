// client/src/main/java/ro/tuiasi/ac/client/ClientMain.java
package ro.tuiasi.ac.client;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ro.tuiasi.ac.common.*;

public class ClientMain {
    private static KafkaProducerUtil producer;
    private static KafkaConsumerUtil consumer;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static volatile boolean sendingData = false;
    
    public static void main(String[] args) {
        System.out.println("Starting client...");
        String bootstrapServers = Config.getKafkaBootstrapServers();
        
        // Producer for sending sensor data
        producer = new KafkaProducerUtil(bootstrapServers);
        
        // Consumer for receiving commands from server
        consumer = new KafkaConsumerUtil(bootstrapServers, "client-group", Config.COMMAND_TOPIC);
        
        System.out.println("Client started.");
        
        // Give consumer time to subscribe
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Listen for commands
        Thread commandListener = new Thread(() -> {
            while (true) {
                Message command = consumer.pollData(Message.class, Duration.ofMillis(1000));
                if (command != null) {
                    System.out.println("[CLIENT RECEIVED] " + command.getVal());
                    handleCommand(command);
                }
            }
        });
        commandListener.start();
        
        // Wait a bit before starting to send data
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        startSendingData();
    }
    
    private static void handleCommand(Message command) {
        System.out.println("[CLIENT HANDLING] Command received: " + command.getVal());
        
        // Example: Respond to server with acknowledgment
        Message ack = new Message();
        ack.setVal("ACK: Received command '" + command.getVal() + "'");
        producer.sendData(Config.SENSOR_DATA_TOPIC, ack);
    }
    
    private static void startSendingData() {
        System.out.println("Client starting to send data...");
        sendingData = true;
        int samplingRate = 3000; // Send every 3 seconds
        
        scheduler.scheduleAtFixedRate(() -> {
            Message data = readSensorData();
            producer.sendData(Config.SENSOR_DATA_TOPIC, data);
            System.out.println("[CLIENT SENT] " + data.getVal());
        }, 0, samplingRate, TimeUnit.MILLISECONDS);
    }
    
    private static Message readSensorData() {
        Message data = new Message();
        data.setVal("Nice connecting to you, server! Timestamp: " + System.currentTimeMillis());
        return data;
    }
    
    private static void stopSendingData() {
        sendingData = false;
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);
    }
}