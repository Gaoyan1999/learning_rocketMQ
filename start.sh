#!/bin/bash

# Start RocketMQ cluster using Docker Compose
echo "Starting RocketMQ cluster..."
docker-compose up -d

echo "Waiting for services to be ready..."
sleep 10

echo "Checking service status..."
docker-compose ps

echo ""
echo "RocketMQ cluster started!"
echo "NameServer: localhost:9876"
echo "Broker: localhost:10909, 10911, 10912"
echo "Proxy: localhost:8080, 8081"
echo ""
echo "To view logs: docker-compose logs -f"
echo "To stop: ./stop.sh"

