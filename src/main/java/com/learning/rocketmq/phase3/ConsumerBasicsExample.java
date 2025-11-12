package com.learning.rocketmq.phase3;

import com.learning.rocketmq.util.RocketMQConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.consumer.SimpleConsumer;
import org.apache.rocketmq.client.apis.message.MessageView;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 3.2: Consumer Basics Example
 * <p>
 * Learning objectives: 1. Create a Consumer instance 2. Subscribe to topics 3. Push consumer
 * (reactive) 4. Pull consumer (polling) 5. Message consumption patterns 6. Consumer tags filtering
 */
public class ConsumerBasicsExample {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerBasicsExample.class);
    private static final String TOPIC = RocketMQConfig.getDefaultTopic();
    private static final String CONSUMER_GROUP = RocketMQConfig.getDefaultConsumerGroup();

    public static void main(String[] args) {
        try {
            logger.info("=== Phase 3.2: Consumer Basics Examples ===\n");

            // Subscribe to topics
            // demonstrateSubscribeToTopics();

            // Thread.sleep(2000);
            //
            // // Push consumer (reactive)
            // demonstratePushConsumer();
            //
            // Thread.sleep(5000);
            //
            // // Pull consumer (polling) - SIMULATED with PushConsumer
            // demonstratePullConsumer();
            //
            // Thread.sleep(5000);
            //
            // // TRUE Pull consumer - using SimpleConsumer (consumer actively pulls)
            demonstratePullConsumer();
            //
            // Thread.sleep(5000);
            //
            // // Message consumption patterns
            // demonstrateConsumptionPatterns();
            //
            // Thread.sleep(2000);
            //
            // // Consumer tags filtering
            // demonstrateTagFiltering();

            // Keep running to demonstrate continuous consumption
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            logger.error("Error in ConsumerBasicsExample", e);
        }
    }

    /**
     * Push consumer (reactive)
     * <p>
     * Push Consumer: - Reactive pattern: Messages are pushed to consumer automatically - Consumer
     * provides a message listener (callback) - Broker pushes messages when available - Better for
     * real-time processing - Simpler to use
     */
    private static void demonstratePushConsumer()
            throws ClientException, InterruptedException, IOException {
        logger.info("\n=== 3. Push Consumer (Reactive) ===");
        logger.info("Push Consumer: Messages are pushed automatically to consumer");

        ClientServiceProvider provider = ClientServiceProvider.loadService();

        FilterExpression filterExpression = new FilterExpression("*", FilterExpressionType.TAG);

        PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(RocketMQConfig.getClientConfiguration())
                .setConsumerGroup(CONSUMER_GROUP + "_Push")
                .setSubscriptionExpressions(Collections.singletonMap(TOPIC, filterExpression))
                // Set message listener - this is called when messages arrive
                .setMessageListener(messageView -> {
                    try {
                        System.out.println(RocketMQConfig.printMessageView(messageView));
                    } catch (Exception e) {
                        logger.error("Error processing message in PushConsumer", e);
                        return ConsumeResult.FAILURE;
                    }
                    return ConsumeResult.SUCCESS;
                }).build();
        // Keep running for a while to demonstrate
        Thread.sleep(10000);
        pushConsumer.close();
    }


    /**
     * TRUE Pull Consumer - Consumer actively pulls messages
     * <p>
     * ✅ This is REAL pull pattern implementation using SimpleConsumer
     * <p>
     * Key differences from PushConsumer: - Consumer actively calls receive() method to fetch
     * messages - Consumer controls exactly when to pull messages - No MessageListener callback -
     * messages are pulled on demand - Manual acknowledgment required after processing - Full
     * control over consumption rate and timing
     * <p>
     * Use cases: - When you need precise control over when to consume messages - Batch processing
     * scenarios - Integration with stream processing frameworks - When you need to implement custom
     * backpressure logic
     */
    private static void demonstratePullConsumer()
            throws ClientException, InterruptedException, IOException {

        ClientServiceProvider provider = ClientServiceProvider.loadService();

        FilterExpression filterExpression = new FilterExpression("*", FilterExpressionType.TAG);

        // ✅ Using SimpleConsumer - this allows true pull pattern
        SimpleConsumer consumer = provider.newSimpleConsumerBuilder()
                .setClientConfiguration(RocketMQConfig.getClientConfiguration())
                .setConsumerGroup(CONSUMER_GROUP + "_Pull")
                .setSubscriptionExpressions(Collections.singletonMap(TOPIC, filterExpression))
                // when the queue is empty, the consumer will wait for the duration to receive messages, if the duration is reached, the consumer will return null.
                .setAwaitDuration(Duration.ofSeconds(30)).build();

        while (true) {
            try {
                // maxMessageNums:5            
                // the time of invisible duration is 30 seconds. If ack is called after the duration, there are will be an error.
                // So the below code means that this consumer needs to process the message within 30 seconds, otherwise the message will be retried by the broker and consumed by other consumers.
                List<MessageView> messages = consumer.receive(1, Duration.ofSeconds(30));
                logger.info("Batch Pulled messages: {}", messages.size());
                if (messages == null || messages.isEmpty()) {
                    logger.info("No messages available, waiting...");
                    Thread.sleep(1000);
                    continue;
                }
                for (MessageView messageView : messages) {
                    // process the message, and sleep for 10 seconds to simulate the message processing time
                    logger.info("Processing message: {}", messageView.getMessageId());
                    System.out.println(RocketMQConfig.printMessageView(messageView));
                    Thread.sleep(10000); // 10 seconds
                    try {
                        // tell the broker that the message has been processed, otherwise the message will be retried by the broker and consumed by other consumers.
                        logger.info("Process Done, Acknowledging message");
                        consumer.ack(messageView);                        
                    } catch (ClientException e) {
                        logger.error("Failed to acknowledge message: {}",
                                messageView.getMessageId(), e);
                    }
                }             
            }

            catch (ClientException e) {
                logger.error("Error receiving message", e);
                Thread.sleep(1000); // Wait before retry
            }
        }

    }

    /**
     * Message consumption patterns
     * <p>
     * Consumption Patterns:
     * - At-least-once: Message may be consumed multiple times (default) -
     * - Exactly-once: Message consumed exactly once (requires idempotency)
     * - ConsumeResult.SUCCESS: Message processed successfully
     * - ConsumeResult.FAILURE: Message processing failed, will be retried
     */
    private static void demonstrateConsumptionPatterns() throws ClientException, IOException {
        logger.info("\n=== 5. Message Consumption Patterns ===");

        ClientServiceProvider provider = ClientServiceProvider.loadService();

        FilterExpression filterExpression = new FilterExpression("*", FilterExpressionType.TAG);

        PushConsumer consumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(RocketMQConfig.getClientConfiguration())
                .setConsumerGroup(CONSUMER_GROUP + "_Pattern")
                .setSubscriptionExpressions(Collections.singletonMap(TOPIC, filterExpression))
                .setMessageListener(messageView -> {
                    try {
                        ByteBuffer bodyBuffer = messageView.getBody();
                        byte[] bodyBytes = new byte[bodyBuffer.remaining()];
                        bodyBuffer.get(bodyBytes);
                        String body = new String(bodyBytes);

                        logger.info("Processing message: {}", body);

                        // Simulate processing logic
                        // Return SUCCESS if processing succeeds
                        // Return FAILURE if processing fails (message will be retried)

                        // Example: Simulate failure for certain messages
                        if (body.contains("fail")) {
                            logger.warn("  → Processing failed, returning FAILURE (will retry)");
                            return ConsumeResult.FAILURE;
                        }

                        logger.info("  → Processing succeeded, returning SUCCESS");
                        return ConsumeResult.SUCCESS;

                    } catch (Exception e) {
                        logger.error("Error processing message", e);
                        return ConsumeResult.FAILURE;
                    }
                }).build();

        logger.info("✓ Consumer with consumption pattern handling created");
        logger.info("\nConsumption patterns:");
        logger.info("  - ConsumeResult.SUCCESS: Message processed, won't be retried");
        logger.info("  - ConsumeResult.FAILURE: Message failed, will be retried");
        logger.info("  - At-least-once: Default pattern (message may be consumed multiple times)");
        logger.info("  - Idempotency: Implement in your business logic to handle duplicates");

        consumer.close();
    }
}

