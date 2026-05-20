package ro.tuiasi.ac.server;

import ro.tuiasi.ac.common.ClientMessage;
import ro.tuiasi.ac.common.Command;
import ro.tuiasi.ac.common.Config;
import ro.tuiasi.ac.common.KafkaConsumerUtil;
import ro.tuiasi.ac.common.KafkaProducerUtil;
import ro.tuiasi.ac.common.KafkaTopicUtil;
import ro.tuiasi.ac.common.ServerMessage;
import ro.tuiasi.ac.common.StartCommand;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the server application.
 * Initializes Kafka communication, receives client messages,
 * processes them, sends responses back to the client,
 * and tracks processing statistics.
 *
 * @author Your Name
 */
public final class ServerMain {
    /** Delay before sending test message in milliseconds. */
    private static final int TEST_MESSAGE_DELAY_MS = 5000;

    /** Handles the viewing of the received CameraFrame
     * data from SensorDataSet. */
    private static ImageViewer viewer;

    /** Kafka producer used for sending messages to clients. */
    private static KafkaProducerUtil producer;

    /** Kafka consumer used for receiving messages from clients. */
    private static KafkaConsumerUtil consumer;

    /** Total number of processed client messages. */
    private static final AtomicInteger MESSAGES_PROCESSED =
                new AtomicInteger(0);

    /** Total accumulated processing time for all messages. */
    private static long totalProcessingTime;

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(ServerMain.class);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ServerMain() {
        // Utility class - no instantiation needed
    }

    /**
     * Starts the server application.
     * Initializes Kafka topics, producer, consumer,
     * begins listening for client messages, and keeps the server running.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(final String[] args) {
        LOG.info("🚀 Starting Server...");
        try {
            viewer = new ImageViewer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String bootstrapServers = Config.getKafkaBootstrapServers();

        // Create topics if they don't exist
        KafkaTopicUtil.createTopicsIfNotExist(
                bootstrapServers,
                Config.CLIENT_TO_SERVER_TOPIC,
                Config.SERVER_TO_CLIENT_TOPIC);

        // Initialize producer and consumer
        producer = new KafkaProducerUtil(bootstrapServers);
        consumer = new KafkaConsumerUtil(
                bootstrapServers,
                "server-group",
                Config.CLIENT_TO_SERVER_TOPIC);

        // Listen for messages from clients
        consumer.listen(ClientMessage.class, message -> {
            ClientMessage clientMsg = (ClientMessage) message;
            long receiveTime = Instant.now().toEpochMilli();
            if (LOG.isInfoEnabled()) {
                LOG.info("📥 Server received: " + clientMsg);
                LOG.info("   ⏱️  Received at: " + receiveTime);
            }
            processClientMessage(clientMsg, receiveTime);
        });

        // Send a test message to client after delay
        try {
            Thread.sleep(TEST_MESSAGE_DELAY_MS);
            sendMessageToClient(new StartCommand());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Keep server running
        keepAlive();
    }

    /**
     * Sends a message from the server to the client.
     *
     * @param content command content to send
     */
    private static void sendMessageToClient(final Command content) {
        ServerMessage message = new ServerMessage(content);
        message.setServerSentTimestamp(Instant.now().toEpochMilli());
        producer.sendMessage(Config.SERVER_TO_CLIENT_TOPIC, message);
        if (LOG.isInfoEnabled()) {
            LOG.info("📤 Server sent: " + message);
        }
    }

    /**
     * Processes a message received from the client.
     * Simulates server-side processing, calculates processing statistics,
     * and sends a response back to the client.
     *
     * @param message     received client message
     * @param receiveTime timestamp when the message was received
     */
    private static void processClientMessage(
            final ClientMessage message,
            final long receiveTime) {

        long startProcessing = System.currentTimeMillis();
        if (LOG.isInfoEnabled()) {
            LOG.info("⚙️ Processing: " + message.getContent());
        }

        viewer.updateFrame(message.getContent().cameraFrame());

        Command content = ProcessingSensor.processSensorDataSet(
                message.getContent());

        long processingTime = System.currentTimeMillis() - startProcessing;

        // Create response message with timing information
        ServerMessage response = new ServerMessage(content, message);
        response.setServerReceivedTimestamp(receiveTime);
        response.setServerSentTimestamp(Instant.now().toEpochMilli());

        // Update server metrics
        int processed = MESSAGES_PROCESSED.incrementAndGet();
        totalProcessingTime += processingTime;
        double avgProcessingTime = totalProcessingTime / (double) processed;

        if (LOG.isInfoEnabled()) {
            LOG.info("   ⚙️  Processing took: {} ms (avg: {} ms)",
                    processingTime, String.format("%.2f", avgProcessingTime));
        }

        // Send response back to client
        producer.sendMessage(Config.SERVER_TO_CLIENT_TOPIC, response);
        if (LOG.isInfoEnabled()) {
            LOG.info("📤 Server sent response: " + response);
            LOG.info("   ⏱️  Sent at: " + response.getServerSentTimestamp());
        }
    }

    /**
     * Keeps the server application running indefinitely.
     */
    private static void keepAlive() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
