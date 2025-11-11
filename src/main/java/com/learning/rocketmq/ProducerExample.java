package com.learning.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);
    // Endpoint address, set to the Proxy address and port list, usually xxx:8080;xxx:8081
    private final static String ENDPOINT = "localhost:8081";
    // The target Topic name for message sending, which needs to be created in advance.
    private final static String TOPIC = "TestTopic";


    public static void main(String[] args) throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(ENDPOINT);
        ClientConfiguration configuration = builder.build();
        // When initializing Producer, communication configuration and pre-bound Topic need to be set.
        Producer producer = provider.newProducerBuilder()
            .setTopics(TOPIC)
            .setClientConfiguration(configuration)
            .build();
        // Sending a normal message.
        Message message = provider.newMessageBuilder()
            .setTopic(TOPIC)
            // Set the message index key, which can be used to accurately find a specific message.
            .setKeys("messageKey")
            // Set the message Tag, used by the consumer to filter messages by specified Tag.
            .setTag("messageTag")
            // Message body.
            .setBody("messageBody".getBytes())
            .build();
        try {
            // Send the message, paying attention to the sending result and catching exceptions.
            SendReceipt sendReceipt = producer.send(message);
            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
        }
        // producer.close();
    }
}