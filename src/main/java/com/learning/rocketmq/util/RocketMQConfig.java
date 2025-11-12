package com.learning.rocketmq.util;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;

/**
 * Utility class for RocketMQ common configurations.
 * Centralizes endpoint, topic, and client configuration setup.
 */
public class RocketMQConfig {
    // Endpoint address, set to the Proxy address and port list
    private static final String ENDPOINT = "localhost:8081";
    
    // Default topic name
    private static final String DEFAULT_TOPIC = "TestTopic";
    
    // Default consumer group
    private static final String DEFAULT_CONSUMER_GROUP = "TestConsumerGroup";

    /**
     * Get the RocketMQ endpoint address.
     * @return endpoint string
     */
    public static String getEndpoint() {
        return ENDPOINT;
    }

    /**
     * Get the default topic name.
     * @return topic name
     */
    public static String getDefaultTopic() {
        return DEFAULT_TOPIC;
    }

    /**
     * Get the default consumer group name.
     * @return consumer group name
     */
    public static String getDefaultConsumerGroup() {
        return DEFAULT_CONSUMER_GROUP;
    }

    /**
     * Create and return a ClientConfiguration instance with default endpoint.
     * @return ClientConfiguration instance
     * @throws ClientException if configuration fails
     */
    public static ClientConfiguration getClientConfiguration() throws ClientException {
        return ClientConfiguration.newBuilder()
            .setEndpoints(ENDPOINT)
            .build();
    }

    /**
     * Create a ClientConfiguration with custom endpoint.
     * @param endpoint custom endpoint address
     * @return ClientConfiguration instance
     * @throws ClientException if configuration fails
     */
    public static ClientConfiguration getClientConfiguration(String endpoint) throws ClientException {
        return ClientConfiguration.newBuilder()
            .setEndpoints(endpoint)
            .build();
    }
}

