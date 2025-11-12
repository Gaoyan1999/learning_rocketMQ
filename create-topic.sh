#!/bin/bash

# Create a topic in RocketMQ
TOPIC_NAME=${1:-TestTopic}
CLUSTER_NAME=${2:-DefaultCluster}

echo "Creating topic: $TOPIC_NAME in cluster: $CLUSTER_NAME"

# Access broker container and create topic
# Use -i instead of -it for non-interactive terminals
docker exec rmqbroker sh mqadmin updatetopic -t $TOPIC_NAME -c $CLUSTER_NAME

if [ $? -eq 0 ]; then
    echo "Topic '$TOPIC_NAME' created successfully!"
    echo ""
    echo "Note: In RocketMQ 5.x with Proxy mode, you may need to send at least one message"
    echo "      to ensure topic route info is synchronized to Proxy."
    echo "      You can run ProducerExample to send a test message."
else
    echo "Failed to create topic '$TOPIC_NAME'"
    exit 1
fi
