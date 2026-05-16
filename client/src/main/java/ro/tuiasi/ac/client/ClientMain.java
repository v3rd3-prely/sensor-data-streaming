package ro.tuiasi.ac.client;

import ro.tuiasi.ac.common.*;
import ro.tuiasi.ac.common.KafkaProducerUtil;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
	/**
	 * Counter used to generate sequential client message numbers.
	 */
	private static final AtomicInteger messageCounter = new AtomicInteger(1);

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
	private static long totalRoundTripTime = 0;
	/**
	 * Minimum measured round-trip time.
	 */
	private static long minRoundTripTime = Long.MAX_VALUE;
	/**
	 * Maximum measured round-trip time.
	 */
	private static long maxRoundTripTime = 0;

	/**
	 * Robot object that collects sensor data sets and executes commands
	 */
	private static Robot robot;

	/**
	 * Default constructor.
	 */
	public ClientMain() {

	}

	/**
	 * Starts the client application. Initializes Kafka topics, producer, consumer,
	 * starts listening for server messages, and begins sending periodic client
	 * messages.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {

		System.out.println("Initializing robot...");

		CameraSensor robotCamera = new CameraSensor("camera", 0, 0);
		LidarSensor robotLeftLidar = new LidarSensor("leftLidar");
		LidarSensor robotRightLidar = new LidarSensor("rightLidar");
		GyroscopeSensor robotGyro = new GyroscopeSensor("gyro");

		SensorSet robotSensorSet = new SensorSet(robotCamera, robotLeftLidar, robotRightLidar, robotGyro);
		robot = new Robot(robotSensorSet);

		System.out.println("Robot initialized");

		System.out.println("🚀 Starting Client...");
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

	/**
	 * Starts sending client messages to the server at a fixed interval. Each sent
	 * message is stored together with its timestamp so that latency can be
	 * calculated when the server response arrives.
	 */
	private static void startSendingMessages() {
		System.out.println("📡 Client starting to send messages every 100 miliseconds...");
		System.out.println("📊 Latency tracking enabled - will measure round-trip time for each message\n");

		scheduler.scheduleAtFixedRate(() -> {
//            String content = "Message #" + messageCounter.getAndIncrement() + " from Client";
			if (robot.isSending()) {

				SensorDataSet content = robot.collectData();
				ClientMessage message = new ClientMessage(content);

				// Store sent time before sending
				long sendTime = System.currentTimeMillis();
				pendingMessages.put(message.getId(), sendTime);

				producer.sendMessage(Config.CLIENT_TO_SERVER_TOPIC, message);
				System.out.println("📤 Client sent: " + message);
				System.out.println("   ⏱️  Sent at: " + sendTime);
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
		System.out.println("⚙️ Processing server response: " + message.getContent());
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

//        if (message.getContent().contains("received")) {
//            System.out.println("✅ Server acknowledged our message!");
//        }

		if (message.getClientMessageId() != null) {
			Long sentTime = pendingMessages.remove(message.getClientMessageId());

		}
	}

	/**
	 * Prints aggregated latency statistics, including average, minimum, and maximum
	 * round-trip time.
	 */
	private static void printRunningStats() {
		double avgRtt = totalRoundTripTime / (double) totalMessages.get();
		System.out.println("\n📊 ─────────── RUNNING LATENCY STATS ───────────");
		System.out.printf("   Messages sent: %d%n", totalMessages.get());
		System.out.printf("   Avg RTT: %.2f ms%n", avgRtt);
		System.out.printf("   Min RTT: %d ms%n", minRoundTripTime);
		System.out.printf("   Max RTT: %d ms%n", maxRoundTripTime);
		System.out.println("   ───────────────────────────────────────────\n");
	}

	/**
	 * Prints the number of messages that are still waiting for a server response.
	 */
	private static void printLatencyStats() {
		// This could be expanded to track min/max/average
		System.out.println("📈 Active pending messages: " + pendingMessages.size());
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