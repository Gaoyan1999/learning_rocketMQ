#!/bin/bash

# Create a topic in RocketMQ
TOPIC_NAME=${1:-TestTopic}
CLUSTER_NAME=${2:-DefaultCluster}

echo "Creating topic: $TOPIC_NAME in cluster: $CLUSTER_NAME"

# Access broker container and create topic
docker exec -it rmqbroker sh mqadmin updatetopic -t $TOPIC_NAME -c $CLUSTER_NAME

# docker exec -it rmqbroker bash
# sh mqadmin updatetopic -t $TOPIC_NAME -c $CLUSTER_NAME

if [ $? -eq 0 ]; then
    echo "Topic '$TOPIC_NAME' created successfully!"
else
    echo "Failed to create topic '$TOPIC_NAME'"
    exit 1
fi

