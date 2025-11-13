#!/bin/bash

# Stop RocketMQ Dashboard
CONTAINER_NAME="rocketmq-dashboard"

echo "Stopping RocketMQ Dashboard..."

if docker ps -a | grep -q "$CONTAINER_NAME"; then
    docker stop $CONTAINER_NAME > /dev/null 2>&1
    echo "✅ Dashboard stopped."
    echo ""
    echo "To remove the container: docker rm $CONTAINER_NAME"
    echo "To start again: docker start $CONTAINER_NAME"
else
    echo "⚠️  Dashboard container not found."
fi

