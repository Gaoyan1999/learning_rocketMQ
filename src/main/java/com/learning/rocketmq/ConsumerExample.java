package com.learning.rocketmq;

import java.io.IOException;
import java.util.Collections;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerExample {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerExample.class);
    private static final String ENDPOINT = "localhost:8081";
    private static final String TOPIC = "TestTopic";
    

    private ConsumerExample() {
    }

    public static void main(String[] args) throws ClientException, IOException, InterruptedException {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        // Endpoint address, set to the Proxy address and port list, usually xxx:8080;xxx:8081
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
            .setEndpoints(ENDPOINT)
            .build();
        // Subscription message filtering rule, indicating subscription to all Tag messages.
        FilterExpression filterExpression = new FilterExpression("*", FilterExpressionType.TAG);
        // Specify the consumer group the consumer belongs to, Group needs to be created in advance.
        String consumerGroup = "TestConsumerGroup";
        // Specify which target Topic to subscribe to, Topic needs to be created in advance.    
        // Initialize PushConsumer
        provider.newPushConsumerBuilder()
            .setClientConfiguration(clientConfiguration)
            // Set the consumer group.
            .setConsumerGroup(consumerGroup)
            // Set pre-bound subscription relationship.
            .setSubscriptionExpressions(Collections.singletonMap(TOPIC, filterExpression))
            // Set the message listener.
            .setMessageListener(messageView -> {
                // Handle messages and return the consumption result.
                logger.info("Consume message successfully, messageId={}", messageView.getMessageId());
                return ConsumeResult.SUCCESS;
            })
            .build();
        Thread.sleep(Long.MAX_VALUE);
        // If PushConsumer is no longer needed, this instance can be closed.
        // pushConsumer.close();
    }
}
