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

import static com.learning.rocketmq.util.RocketMQConfig.printMessageView;

public class ConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerExample.class);

    private ConsumerExample() {
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
