package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.CameraSensor;
import ro.tuiasi.ac.common.ClientMessage;
import ro.tuiasi.ac.common.Config;
import ro.tuiasi.ac.common.GyroscopeSensor;
import ro.tuiasi.ac.common.KafkaConsumerUtil;
import ro.tuiasi.ac.common.KafkaProducerUtil;
import ro.tuiasi.ac.common.KafkaTopicUtil;
import ro.tuiasi.ac.common.LidarSensor;
import ro.tuiasi.ac.common.SensorDataSet;
import ro.tuiasi.ac.common.SensorSet;
import ro.tuiasi.ac.common.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the client application.
 * Initializes Kafka communication, sends periodic messages to the server,
 * receives server responses, and tracks message round-trip latency.
 *
 * @author Your Name
 */
public final class ClientMain {

    /** Initial delay for sending messages in milliseconds. */
    private static final int INITIAL_DELAY_MS = 3000;

    /** Interval between messages in milliseconds. */
    private static final int SEND_INTERVAL_MS = 100;

    /** Number of messages after which to print stats. */
    private static final int STATS_INTERVAL = 5;

    /** Kafka producer used to send messages from the client to the server. */
    private static KafkaProducerUtil producer;

    /** Kafka consumer used to receive messages from the server. */
    private static KafkaConsumerUtil consumer;

    /** Scheduler used to send messages periodically. */
    private static ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    /**
     * Stores sent message ids and their send timestamps.
     * Used to calculate round-trip latency when responses arrive.
     */
    private static final ConcurrentHashMap<String, Long> PENDING_MESSAGES =
            new ConcurrentHashMap<>();

    /** Total number of messages for which latency was calculated. */
    private static final AtomicInteger TOTAL_MESSAGES = new AtomicInteger(0);

    /** Sum of all measured round-trip times. */
    private static long totalRoundTripTime;

    /** Minimum measured round-trip time. */
    private static long minRoundTripTime = Long.MAX_VALUE;

    /** Maximum measured round-trip time. */
    private static long maxRoundTripTime;

    /** Logger for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(ClientMain.class);

    /** Robot object that collects sensor data sets and executes commands. */
    private static Robot robot;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private ClientMain() {
        // Utility class - no instantiation needed
    }

    /**
     * Starts the client application.
     * Initializes Kafka topics, producer, consumer, starts listening for server
     * messages, and begins sending periodic client messages.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(final String[] args) {
        LOG.info("Initializing robot...");

        CameraSensor robotCamera = new CameraSensor("camera", 0, 0);
        LidarSensor robotLeftLidar = new LidarSensor("leftLidar");
        LidarSensor robotRightLidar = new LidarSensor("rightLidar");
        GyroscopeSensor robotGyro = new GyroscopeSensor("gyro");

        SensorSet robotSensorSet = new SensorSet(
                robotCamera,
                robotLeftLidar,
                robotRightLidar,
                robotGyro);
        robot = new Robot(robotSensorSet);

        LOG.info("Robot initialized");
        LOG.info("🚀 Starting Client...");

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
                "client-group",
                Config.SERVER_TO_CLIENT_TOPIC);

        // Listen for messages from server
        consumer.listen(ServerMessage.class, message -> {
            ServerMessage serverMsg = (ServerMessage) message;
            if (LOG.isInfoEnabled()) {
                LOG.info("📥 Client received: " + serverMsg);
            }
            handleServerMessage(serverMsg);
        });

        LOG.info("✅ Client ready and listening");

        // Start sending periodic messages to server after delay
        try {
            Thread.sleep(INITIAL_DELAY_MS);
            startSendingMessages();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Keep client running
        keepAlive();
    }

    /**
     * Starts sending client messages to the server at a fixed interval.
     * Each sent message is stored together with its timestamp so that latency
     * can be calculated when the server response arrives.
     */
    private static void startSendingMessages() {
        LOG.info("📡 Client starting to send messages every 100 miliseconds...");
        LOG.info("📊 Latency tracking enabled - will measure round-trip time\n");

        scheduler.scheduleAtFixedRate(() -> {
            if (robot.isSending()) {
                SensorDataSet content = robot.collectData();
                ClientMessage message = new ClientMessage(content);

                // Store sent time before sending
                long sendTime = System.currentTimeMillis();
                PENDING_MESSAGES.put(message.getId(), sendTime);

                producer.sendMessage(Config.CLIENT_TO_SERVER_TOPIC, message);
                if (LOG.isInfoEnabled()) {
                    LOG.info("📤 Client sent: " + message);
                    LOG.info("   ⏱️  Sent at: " + sendTime);
                }
            }
        }, 0, SEND_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes a message received from the server.
     * If the message is a response to a previously sent client message,
     * calculates round-trip latency and updates statistics.
     *
     * @param message server message received from Kafka
     */
    private static void handleServerMessage(final ServerMessage message) {
        if (LOG.isInfoEnabled()) {
            LOG.info("⚙️ Processing server response: " + message.getContent());
        }
        robot.executeCommand(message.getContent());

        // Check if this is a response to a client message
        if (message.getClientMessageId() != null) {
            Long sentTime = PENDING_MESSAGES.remove(
                     message.getClientMessageId());

            if (sentTime != null) {
                long receivedTime = System.currentTimeMillis();
                long roundTripTime = receivedTime - sentTime;

                // Update statistics
                TOTAL_MESSAGES.incrementAndGet();
                totalRoundTripTime += roundTripTime;
                minRoundTripTime = Math.min(minRoundTripTime, roundTripTime);
                maxRoundTripTime = Math.max(maxRoundTripTime, roundTripTime);

                // Calculate server processing time
                long serverProcessingTime = message.getServerSentTimestamp()
                        - message.getServerReceivedTimestamp();
                long networkTime = roundTripTime - serverProcessingTime;

                if (LOG.isInfoEnabled()) {
                    LOG.info("📊 ═════════════ LATENCY REPORT ═════════════");
                    LOG.info("   📨 Message ID: {}",
                            message.getClientMessageId());
                    LOG.info("   ⏰ Client sent: {}",
                            sentTime);
                    LOG.info("   🖥️  Server received: {}",
                            message.getServerReceivedTimestamp());
                    LOG.info("   🖥️  Server sent: {}",
                            message.getServerSentTimestamp());
                    LOG.info("   📱 Client received: {}",
                            receivedTime);
                    // Line 189 - fixed (shorter divider)
                    LOG.info("   ───────────────────────────────");
                    LOG.info("   ⚙️  Server processing: {} ms",
                            serverProcessingTime);
                    LOG.info("   🌐 Network round-trip: {} ms",
                            networkTime);
                    LOG.info("   🔄 Total round-trip: {} ms",
                            roundTripTime);
                    // Lines 207-208 - fixed (shorter divider)
                    LOG.info("   ═══════════════════════════════\n");
                }

                printLatencyStats();

                // Print running average every STATS_INTERVAL messages
                if (TOTAL_MESSAGES.get() % STATS_INTERVAL == 0) {
                    printRunningStats();
                }
            } else {
                if (LOG.isInfoEnabled()) {
                    LOG.info("⚠️ Received response for unknown message ID: "
                            + message.getClientMessageId());
                }
            }
        } else {
            LOG.info("💬 Server-initiated message (no latency tracking)");
        }

        if (message.getClientMessageId() != null) {
            // Line 220 - fixed (removed duplicate removal)
            PENDING_MESSAGES.remove(message.getClientMessageId());
        }
    }

    /**
     * Prints aggregated latency statistics.
     * Includes average, minimum, and maximum round-trip time.
     */
    private static void printRunningStats() {
        double avgRtt = totalRoundTripTime / (double) TOTAL_MESSAGES.get();
        if (LOG.isInfoEnabled()) {
            LOG.info("\n📊 ─────────── RUNNING LATENCY STATS ───────────");
            LOG.info("   Messages sent: {}", TOTAL_MESSAGES.get());
            LOG.info("   Avg RTT: {} ms", String.format("%.2f", avgRtt));
            LOG.info("   Min RTT: {} ms", minRoundTripTime);
            LOG.info("   Max RTT: {} ms", maxRoundTripTime);
            LOG.info("   ───────────────────────────────────────────\n");
        }
    }

    /**
     * Prints the number of messages waiting for a server response.
     */
    private static void printLatencyStats() {
        if (LOG.isInfoEnabled()) {
            LOG.info("📈 Active pending messages: " + PENDING_MESSAGES.size());
        }
    }

    /**
     * Keeps the client application running indefinitely.
     */
    private static void keepAlive() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
