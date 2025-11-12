package com.learning.rocketmq.util;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Utility class for RocketMQ common configurations. Centralizes endpoint,
 * topic, and client
 * configuration setup.
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
   *
   * @return endpoint string
   */
  public static String getEndpoint() {
    return ENDPOINT;
  }

  /**
   * Get the default topic name.
   *
   * @return topic name
   */
  public static String getDefaultTopic() {
    return DEFAULT_TOPIC;
  }

  /**
   * Get the default consumer group name.
   *
   * @return consumer group name
   */
  public static String getDefaultConsumerGroup() {
    return DEFAULT_CONSUMER_GROUP;
  }

  /**
   * Create and return a ClientConfiguration instance with default endpoint.
   *
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
   *
   * @param endpoint custom endpoint address
   * @return ClientConfiguration instance
   * @throws ClientException if configuration fails
   */
  public static ClientConfiguration getClientConfiguration(String endpoint) throws ClientException {
    return ClientConfiguration.newBuilder()
        .setEndpoints(endpoint)
        .build();
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
}
