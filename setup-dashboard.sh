#!/bin/bash

# RocketMQ Dashboard Setup Script
# This script sets up RocketMQ Dashboard to monitor your RocketMQ cluster

set -e  # Exit on error

echo "=========================================="
echo "RocketMQ Dashboard Setup"
echo "=========================================="
echo ""

# Configuration
DASHBOARD_PORT=8082
NAMESRV_ADDR="rmqnamesrv:9876"  # Use container name when on same network
DASHBOARD_IMAGE="apacherocketmq/rocketmq-dashboard:latest"
CONTAINER_NAME="rocketmq-dashboard"

# Detect docker-compose network name
# Docker Compose creates networks as: <directory>_<network_name>
NETWORK_NAME=$(docker network ls | grep rocketmq | grep -v bridge | awk '{print $2}' | head -1)
if [ -z "$NETWORK_NAME" ]; then
    # Fallback: try to get network from running containers
    NETWORK_NAME=$(docker inspect rmqnamesrv 2>/dev/null | grep -A 10 "Networks" | grep -o '"[^"]*rocketmq[^"]*"' | head -1 | tr -d '"')
fi

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Check if RocketMQ services are running
echo "📋 Checking RocketMQ services..."
if ! docker ps | grep -q "rmqnamesrv\|rmqbroker"; then
    echo "⚠️  Warning: RocketMQ services don't seem to be running."
    echo "   Please start RocketMQ first using: ./start.sh"
    read -p "   Do you want to continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check if dashboard container already exists
if docker ps -a | grep -q "$CONTAINER_NAME"; then
    echo "⚠️  Dashboard container already exists."
    read -p "   Do you want to remove and recreate it? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🗑️  Removing existing dashboard container..."
        docker stop $CONTAINER_NAME > /dev/null 2>&1 || true
        docker rm $CONTAINER_NAME > /dev/null 2>&1 || true
    else
        echo "ℹ️  Using existing container. To start it: docker start $CONTAINER_NAME"
        exit 0
    fi
fi

# Pull the latest dashboard image
echo "📥 Pulling RocketMQ Dashboard image..."
docker pull $DASHBOARD_IMAGE

# Run the dashboard container
echo "🚀 Starting RocketMQ Dashboard..."

if [ -n "$NETWORK_NAME" ]; then
    echo "   Connecting to network: $NETWORK_NAME"
    docker run -d \
      --name $CONTAINER_NAME \
      --network $NETWORK_NAME \
      -e "JAVA_OPTS=-Drocketmq.namesrv.addr=$NAMESRV_ADDR -Dserver.port=8080" \
      -p $DASHBOARD_PORT:8080 \
      $DASHBOARD_IMAGE
else
    echo "   ⚠️  Could not detect network, using host networking mode"
    echo "   Using NameServer address: 127.0.0.1:9876"
    docker run -d \
      --name $CONTAINER_NAME \
      -e "JAVA_OPTS=-Drocketmq.namesrv.addr=127.0.0.1:9876 -Dserver.port=8080" \
      -p $DASHBOARD_PORT:8080 \
      $DASHBOARD_IMAGE
fi

# Wait for dashboard to be ready
echo "⏳ Waiting for dashboard to start..."
sleep 5

# Check if container is running
if docker ps | grep -q "$CONTAINER_NAME"; then
    echo ""
    echo "✅ RocketMQ Dashboard started successfully!"
    echo ""
    echo "📊 Dashboard URL: http://localhost:$DASHBOARD_PORT"
    echo "🔗 NameServer Address: $NAMESRV_ADDR"
    echo ""
    echo "📝 Useful commands:"
    echo "   - View logs: docker logs -f $CONTAINER_NAME"
    echo "   - Stop dashboard: docker stop $CONTAINER_NAME"
    echo "   - Start dashboard: docker start $CONTAINER_NAME"
    echo "   - Remove dashboard: docker rm -f $CONTAINER_NAME"
    echo ""
    echo "🌐 Open http://localhost:$DASHBOARD_PORT in your browser to access the dashboard."
else
    echo "❌ Error: Dashboard container failed to start."
    echo "   Check logs with: docker logs $CONTAINER_NAME"
    exit 1
fi

