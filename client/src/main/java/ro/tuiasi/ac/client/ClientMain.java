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
//import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the client application. Initializes Kafka communication,
 * sends periodic messages to the server, receives server responses, and tracks
 * message round-trip latency.
 */
public class ClientMain {

	/**
	 * Kafka producer used to send messages from the client to the server.
	 */
	private static KafkaProducerUtil producer;
	/**
	 * Kafka consumer used to receive messages from the server.
	 */
	private static KafkaConsumerUtil consumer;
	/**
	 * Scheduler used to send messages periodically.
	 */
	private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	// Store sent messages to calculate latency when response arrives
	/**
	 * Stores sent message ids and their send timestamps. Used to calculate
	 * round-trip latency when responses arrive.
	 */
	private static final ConcurrentHashMap<String, Long> pendingMessages = new ConcurrentHashMap<>();

	/**
	 * Total number of messages for which latency was calculated.
	 */
	private static final AtomicInteger totalMessages = new AtomicInteger(0);

	/**
	 * Sum of all measured round-trip times.
	 */
	private static long totalRoundTripTime;
	/**
	 * Minimum measured round-trip time.
	 */
	private static long minRoundTripTime = Long.MAX_VALUE;
	/**
	 * Maximum measured round-trip time.
	 */
	private static long maxRoundTripTime;

	/**
	 * Logger for messages acknowledging messages received
	 */
	private static final Logger log = LoggerFactory.getLogger(ClientMain.class);

	/**
	 * Robot object that collects sensor data sets and executes commands
	 */
	private static Robot robot;

	/**
	 * Default constructor.
	 */
	private ClientMain() {

	}

	/**
	 * Starts the client application. Initializes Kafka topics, producer, consumer,
	 * starts listening for server messages, and begins sending periodic client
	 * messages.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
//		log.info("Initializing robot...");
		log.info("Initializing robot...");

		CameraSensor robotCamera = new CameraSensor("camera", 0, 0);
		LidarSensor robotLeftLidar = new LidarSensor("leftLidar");
		LidarSensor robotRightLidar = new LidarSensor("rightLidar");
		GyroscopeSensor robotGyro = new GyroscopeSensor("gyro");

		SensorSet robotSensorSet = new SensorSet(robotCamera, robotLeftLidar, robotRightLidar, robotGyro);
		robot = new Robot(robotSensorSet);

//		log.info("Robot initialized");
		log.info("Robot initialized");

//		log.info("🚀 Starting Client...");
		log.info("🚀 Starting Client...");
		String bootstrapServers = Config.getKafkaBootstrapServers();

		// Create topics if they don't exist
		KafkaTopicUtil.createTopicsIfNotExist(bootstrapServers, Config.CLIENT_TO_SERVER_TOPIC,
				Config.SERVER_TO_CLIENT_TOPIC);

		// Initialize producer and consumer
		producer = new KafkaProducerUtil(bootstrapServers);
		consumer = new KafkaConsumerUtil(bootstrapServers, "client-group", Config.SERVER_TO_CLIENT_TOPIC);

		// Listen for messages from server
		consumer.listen(ServerMessage.class, message -> {
			ServerMessage serverMsg = (ServerMessage) message;
			if (log.isInfoEnabled()) {
				log.info("📥 Client received: " + serverMsg);
			}
			handleServerMessage(serverMsg);
		});

		log.info("✅ Client ready and listening");

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

	/**
	 * Starts sending client messages to the server at a fixed interval. Each sent
	 * message is stored together with its timestamp so that latency can be
	 * calculated when the server response arrives.
	 */
	private static void startSendingMessages() {
		log.info("📡 Client starting to send messages every 100 miliseconds...");
		log.info("📊 Latency tracking enabled - will measure round-trip time for each message\n");

		scheduler.scheduleAtFixedRate(() -> {
//            String content = "Message #" + messageCounter.getAndIncrement() + " from Client";
			if (robot.isSending()) {

				SensorDataSet content = robot.collectData();
				ClientMessage message = new ClientMessage(content);

				// Store sent time before sending
				long sendTime = System.currentTimeMillis();
				pendingMessages.put(message.getId(), sendTime);

				producer.sendMessage(Config.CLIENT_TO_SERVER_TOPIC, message);
				if (log.isInfoEnabled()) {
					log.info("📤 Client sent: " + message);
					log.info("   ⏱️  Sent at: " + sendTime);
				}
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * Processes a message received from the server. If the message is a response to
	 * a previously sent client message, the method calculates round-trip latency
	 * and updates statistics.
	 *
	 * @param message server message received from Kafka
	 */
	private static void handleServerMessage(ServerMessage message) {
		if (log.isInfoEnabled()) {
			log.info("⚙️ Processing server response: " + message.getContent());
		}
		robot.executeCommand(message.getContent());
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

				if (log.isInfoEnabled()) {
					log.info("\n📊 ═══════════════ LATENCY REPORT ═══════════════");
					log.info("   📨 Message ID: {}", message.getClientMessageId());
					log.info("   ⏰ Client sent:        {}", sentTime);
					log.info("   🖥️  Server received:    {}", message.getServerReceivedTimestamp());
					log.info("   🖥️  Server sent:        {}", message.getServerSentTimestamp());
					log.info("   📱 Client received:    {}", receivedTime);
					log.info("   ───────────────────────────────────────────");
					log.info("   ⚙️  Server processing:   {} ms", serverProcessingTime);
					log.info("   🌐 Network round-trip:  {} ms", networkTime);
					log.info("   🔄 Total round-trip:    {} ms", roundTripTime);
					log.info("   ═══════════════════════════════════════════\n");
				}

				// Optional: Calculate statistics
				printLatencyStats();

				// Print running average every 5 messages
				if (totalMessages.get() % 5 == 0) {
					printRunningStats();
				}

			} else {
				if (log.isInfoEnabled()) {
					log.info("⚠️ Received response for unknown message ID: " + message.getClientMessageId());
				}
			}
		} else {
			log.info("💬 Server-initiated message (no latency tracking)");
		}

//        if (message.getContent().contains("received")) {
//            log.info("✅ Server acknowledged our message!");
//        }

		if (message.getClientMessageId() != null) {
			pendingMessages.remove(message.getClientMessageId());

		}
	}

	/**
	 * Prints aggregated latency statistics, including average, minimum, and maximum
	 * round-trip time.
	 */
	private static void printRunningStats() {
		double avgRtt = totalRoundTripTime / (double) totalMessages.get();
		if (log.isInfoEnabled()) {
			log.info("\n📊 ─────────── RUNNING LATENCY STATS ───────────");
			log.info("   Messages sent: {}", totalMessages.get());
			log.info("   Avg RTT: {} ms", avgRtt);
			log.info("   Min RTT: {} ms", minRoundTripTime);
			log.info("   Max RTT: {} ms", maxRoundTripTime);
			log.info("   ───────────────────────────────────────────\n");
		}
	}

	/**
	 * Prints the number of messages that are still waiting for a server response.
	 */
	private static void printLatencyStats() {
		// This could be expanded to track min/max/average
		if (log.isInfoEnabled()) {
			log.info("📈 Active pending messages: " + pendingMessages.size());
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
