package com.learning.rocketmq;

import com.learning.rocketmq.util.RocketMQConfig;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);

    public static Message getDefaultMessage(ClientServiceProvider provider) {
        // Sending a normal message.
        return provider.newMessageBuilder()
                .setTopic(RocketMQConfig.getDefaultTopic())
                // Set the message index key, which can be used to accurately find a specific
                // message.
                .setKeys("messageKey")
                // Set the message Tag, used by the consumer to filter messages by specified
                // Tag.
                .setTag("messageTag")
                // Message body.
                .setBody("messageBody".getBytes())
                .build();
    }

    public static void main(String[] args) throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        // When initializing Producer, communication configuration and pre-bound Topic
        // need to be set.
        Producer producer = provider.newProducerBuilder()
                .setTopics(RocketMQConfig.getDefaultTopic())
                .setClientConfiguration(RocketMQConfig.getClientConfiguration())
                .build();
        try {
            // Send the message, paying attention to the sending result and catching
            // exceptions.
            // Send the message every 5 seconds in a loop
            while (true) {
                SendReceipt sendReceipt = producer.send(getDefaultMessage(provider));
                logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.warn("Producer thread interrupted", ie);
                    break;
                }
            }
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
        }
        // producer.close();
    }
}