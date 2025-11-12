package com.learning.rocketmq.phase3;

import com.learning.rocketmq.util.RocketMQConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 3.1: Producer Basics Example
 * 
 * Learning objectives:
 * 1. Create a Producer instance
 * 2. Send synchronous messages
 * 3. Send asynchronous messages
 * 4. Send one-way messages
 * 5. Handle send results and exceptions
 * 6. Message keys and properties
 */
public class ProducerBasicsExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerBasicsExample.class);
    private static final String TOPIC = RocketMQConfig.getDefaultTopic();

    public static void main(String[] args) {
        Producer producer = null;
        try {
            // 1. Create a Producer instance
            producer = createProducer();
            
            // 2. Send synchronous messages
            demonstrateSynchronousMessages(producer);
            
            // Wait a bit between examples
            Thread.sleep(2000);
            
            // 3. Send asynchronous messages
            demonstrateAsynchronousMessages(producer);
            
            // Wait a bit between examples
            Thread.sleep(2000);
            
            // 4. Send one-way messages
            demonstrateOneWayMessages(producer);
            
            // Wait a bit between examples
            Thread.sleep(2000);
            
            // 6. Demonstrate message keys and properties
            demonstrateMessageKeysAndProperties(producer);
            
            // Wait a bit before closing
            Thread.sleep(3000);
            
        } catch (Exception e) {
            logger.error("Error in ProducerBasicsExample", e);
        } finally {
            // Close producer when done
            if (producer != null) {
                try {
                    producer.close();
                    logger.info("Producer closed successfully");
                } catch (Exception e) {
                    logger.error("Error closing producer", e);
                }
            }
        }
    }

    /**
     * 1. Create a Producer instance
     * 
     * Steps:
     * - Load ClientServiceProvider
     * - Create ClientConfiguration with endpoint
     * - Build Producer with topic and configuration
     */
    private static Producer createProducer() throws ClientException {
        logger.info("=== 1. Creating Producer Instance ===");
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        Producer producer = provider.newProducerBuilder()
            .setTopics(TOPIC)
            .setClientConfiguration(RocketMQConfig.getClientConfiguration())
            .build();
        logger.info("Producer created successfully for topic: {}", TOPIC);
        return producer;
    }

    /**
     * 2. Send synchronous messages
     * 
     * Synchronous sending: 
     * - Blocks until the result is received
     * - Returns SendReceipt with messageId
     * - Can throw ClientException if send fails
     * - Use when you need to ensure message is sent before continuing
     */
    private static void demonstrateSynchronousMessages(Producer producer) throws ClientException {
        logger.info("\n=== 2. Demonstrating Synchronous Messages ===");
        logger.info("Synchronous sending: blocks until result is received");
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        
        for (int i = 1; i <= 3; i++) {
            Message message = provider.newMessageBuilder()
                .setTopic(TOPIC)
                .setKeys("sync-key-" + i)
                .setTag("sync")
                .setBody(("Synchronous message #" + i).getBytes())
                .build();
            
            try {
                // Send synchronously - blocks until result is received
                SendReceipt sendReceipt = producer.send(message);
                logger.info("✓ Synchronous message sent successfully");
                logger.info("  MessageId: {}", sendReceipt.getMessageId());
                logger.info("  Key: {}", message.getKeys());
            } catch (ClientException e) {
                // 5. Handle send results and exceptions
                logger.error("✗ Failed to send synchronous message #{}", i, e);
            }
        }
    }

    /**
     * 3. Send asynchronous messages
     * 
     * Asynchronous sending:
     * - Non-blocking, returns immediately
     * - Result handled via CompletableFuture callback
     * - Use when you don't need to wait for send result
     * - Better performance for high-throughput scenarios
     */
    private static void demonstrateAsynchronousMessages(Producer producer) throws InterruptedException {
        logger.info("\n=== 3. Demonstrating Asynchronous Messages ===");
        logger.info("Asynchronous sending: non-blocking, result handled via callback");
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        CountDownLatch latch = new CountDownLatch(3);
        
        for (int i = 1; i <= 3; i++) {
            final int messageNum = i;
            Message message = provider.newMessageBuilder()
                .setTopic(TOPIC)
                .setKeys("async-key-" + i)
                .setTag("async")
                .setBody(("Asynchronous message #" + i).getBytes())
                .build();
            
            // Send asynchronously - non-blocking
            producer.sendAsync(message)
                .thenAccept(sendReceipt -> {
                    logger.info("✓ Asynchronous message sent successfully");
                    logger.info("  MessageId: {}", sendReceipt.getMessageId());
                    logger.info("  Key: {}", message.getKeys());
                    latch.countDown();
                })
                .exceptionally(throwable -> {
                    // 5. Handle send results and exceptions
                    logger.error("✗ Failed to send asynchronous message #{}", messageNum, throwable);
                    latch.countDown();
                    return null;
                });
        }
        
        // Wait for all async messages to complete (with timeout)
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        if (!completed) {
            logger.warn("Not all asynchronous messages completed within timeout");
        }
    }

    /**
     * 4. Send one-way messages
     * 
     * One-way sending:
     * - Fire and forget, no result returned
     * - Fastest sending method
     * - No guarantee of delivery (use with caution)
     * - Use for logging, metrics, or non-critical messages
     * 
     * Note: RocketMQ 5.x client may not have sendOneway method.
     * If not available, we'll demonstrate the concept with async sending.
     */
    private static void demonstrateOneWayMessages(Producer producer) {
        logger.info("\n=== 4. Demonstrating One-Way Messages ===");
        logger.info("One-way sending: fire and forget, no result returned");
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        
        for (int i = 1; i <= 3; i++) {
            final int messageNum = i;
            Message message = provider.newMessageBuilder()
                .setTopic(TOPIC)
                .setKeys("oneway-key-" + i)
                .setTag("oneway")
                .setBody(("One-way message #" + i).getBytes())
                .build();
            
            try {
                // Note: RocketMQ 5.x may not have sendOneway method
                // Using async send without waiting for result as alternative
                producer.sendAsync(message)
                    .thenAccept(sendReceipt -> {
                        logger.info("One-way message sent (via async) - Key: {}", message.getKeys());
                    })
                    .exceptionally(throwable -> {
                        logger.error("Failed to send one-way message #{}", messageNum, throwable);
                        return null;
                    });
                logger.info("One-way message sent (fire and forget) - Key: {}", message.getKeys());
            } catch (Exception e) {
                // 5. Handle send results and exceptions
                logger.error("✗ Failed to send one-way message #{}", messageNum, e);
            }
        }
    }

    /**
     * 6. Message keys and properties
     * 
     * Message Keys:
     * - Used for message lookup and duplicate detection
     * - Can set multiple keys
     * - Important for message tracing
     * 
     * Message Body:
     * - Contains the actual business data
     * - Usually JSON, XML, or other structured data
     * - This is what the consumer processes
     * 
     * Message Properties:
     * - Metadata for filtering, routing, and conditional processing
     * - NOT for business data (business data should be in Body)
     * - Examples: source, priority, region, version, traceId, environment
     * - Can be used in SQL92 filter expressions
     */
    private static void demonstrateMessageKeysAndProperties(Producer producer) throws ClientException {
        logger.info("\n=== 6. Demonstrating Message Keys and Properties ===");
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        
        // Business data should be in Body (as JSON, XML, etc.)
        // This is the actual data the consumer needs to process
        String orderJson = String.format(
            "{\"orderId\":\"ORDER-67890\",\"userId\":\"12345\",\"amount\":99.99,\"currency\":\"USD\",\"timestamp\":%d}",
            System.currentTimeMillis()
        );
        
        // Properties are for METADATA only - used for filtering, routing, etc.
        // Examples: source, priority, region, version, traceId, environment
        Map<String, String> metadataProperties = new HashMap<>();
        metadataProperties.put("source", "order-service");
        metadataProperties.put("priority", "high");
        metadataProperties.put("region", "us-east");
        metadataProperties.put("version", "v1.0");
        metadataProperties.put("traceId", "trace-12345");
        metadataProperties.put("environment", "production");
        
        Message message = provider.newMessageBuilder()
            .setTopic(TOPIC)
            // Set message keys - used for message lookup and duplicate detection
            // Can set multiple keys
            .setKeys("order-key-001", "user-key-12345")
            .setTag("order")
            // Body contains BUSINESS DATA (JSON in this example)
            .setBody(orderJson.getBytes())
            // Properties contain METADATA only - for filtering, routing, etc.
            .addProperty("source", metadataProperties.get("source"))
            .addProperty("priority", metadataProperties.get("priority"))
            .addProperty("region", metadataProperties.get("region"))
            .addProperty("version", metadataProperties.get("version"))
            .addProperty("traceId", metadataProperties.get("traceId"))
            .addProperty("environment", metadataProperties.get("environment"))
            .build();
        
        try {
            SendReceipt sendReceipt = producer.send(message);
            logger.info("✓ Message with keys and properties sent successfully");
            logger.info("  MessageId: {}", sendReceipt.getMessageId());
            logger.info("  Keys: {}", message.getKeys());
            logger.info("  Body (Business Data): {}", orderJson);
            logger.info("  Properties (Metadata): {}", metadataProperties);            
        } catch (ClientException e) {
            // 5. Handle send results and exceptions
            logger.error("✗ Failed to send message with keys and properties", e);
        }
    }
}

