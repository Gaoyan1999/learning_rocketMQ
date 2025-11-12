package com.learning.rocketmq;

import com.learning.rocketmq.util.RocketMQConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerExample.class);

    private ConsumerExample() {
    }

    public static String printMessageView(MessageView messageView) {
        ByteBuffer bodyBuffer = messageView.getBody();
        byte[] bodyBytes = new byte[bodyBuffer.remaining()];
        bodyBuffer.get(bodyBytes);
        String body = new String(bodyBytes);

        Map<String, String> properties = messageView.getProperties();

        // Collect all message info into one log entry to avoid interleaving
        // when multiple threads process messages concurrently
        StringBuilder messageInfo = new StringBuilder();
        messageInfo.append("\n--------------------------------\n");
        messageInfo.append("Consume message successfully\n");
        messageInfo.append("  MessageId: ").append(messageView.getMessageId()).append("\n");
        messageInfo.append("  Topic: ").append(messageView.getTopic()).append("\n");
        messageInfo.append("  Tag: ").append(messageView.getTag().orElse("N/A")).append("\n");
        messageInfo.append("  Keys: ").append(messageView.getKeys()).append("\n");
        messageInfo.append("  Body: ").append(body).append("\n");
        if (properties != null && !properties.isEmpty()) {
            messageInfo.append("  Properties: ").append(properties).append("\n");
        }
        messageInfo.append("--------------------------------");
        return messageInfo.toString();
    }

    public static void main(String[] args) throws ClientException, IOException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();

        logger.info("Starting Consumer...");
        logger.info("Topic: {}", RocketMQConfig.getDefaultTopic());
        logger.info("Consumer Group: {}", RocketMQConfig.getDefaultConsumerGroup());

        // Initialize PushConsumer
        provider.newPushConsumerBuilder()
                .setClientConfiguration(RocketMQConfig.getClientConfiguration())
                // Set the consumer group.
                .setConsumerGroup(RocketMQConfig.getDefaultConsumerGroup())
                // Set pre-bound subscription relationship.
                .setSubscriptionExpressions(
                        Collections.singletonMap(RocketMQConfig.getDefaultTopic(),
                                new FilterExpression("*", FilterExpressionType.TAG)))
                // Set the message listener.
                .setMessageListener(messageView -> {
                    // Handle messages and return the consumption result.
                    try {
                        System.out.println(printMessageView(messageView));
                    } catch (Exception e) {
                        logger.error("Error processing message", e);
                        return ConsumeResult.FAILURE;
                    }
                    return ConsumeResult.SUCCESS;
                })
                .build();

        logger.info("Consumer started successfully. Waiting for messages...");
        Thread.sleep(Long.MAX_VALUE);
        // If PushConsumer is no longer needed, this instance can be closed.
        // pushConsumer.close();
    }
}
