# Phase 2: Installation & Setup with Docker Compose

This guide will help you set up RocketMQ using Docker Compose and run your first producer and consumer examples.

## Prerequisites

- Docker and Docker Compose installed
- JDK 1.8 or higher
- Maven 3.6 or higher

## Step-by-Step Guide

### 2.1 Start RocketMQ Cluster

Start all RocketMQ services using Docker Compose:

```bash
./start.sh
```

Or manually:
```bash
docker-compose up -d
```

This will start key components:
- **NameServer**: Service discovery and routing (`rmqnamesrv:9876`)
- **Broker**: Message storage and forwarding (`rmqbroker`)
- **Proxy**: Client gateway for message operations (`rmqproxy:8080,8081`)

Verify services are running:
```bash
docker-compose ps
```

View logs:
```bash
docker-compose logs -f
```

### 2.2 Create Topic

Create a topic named `TestTopic`:

```bash
./create-topic.sh TestTopic
```

Or manually:
```bash
docker exec -it rmqbroker sh mqadmin updatetopic -t TestTopic -c DefaultCluster
```

### 2.3 Build the Project

```bash
mvn clean compile
```

### 2.4 Run Producer

In one terminal, run the producer to send messages:

```bash
./run-producer.sh
```

Or manually:
```bash
mvn exec:java -Dexec.mainClass="com.learning.rocketmq.ProducerExample"
```

The producer will send 10 messages to the `TestTopic` topic.

### 2.5 Run Consumer

In another terminal, run the consumer to receive messages:

```bash
./run-consumer.sh
```

Or manually:
```bash
mvn exec:java -Dexec.mainClass="com.learning.rocketmq.ConsumerExample"
```

The consumer will receive and print the messages sent by the producer.

### 2.6 Stop Services

When you're done, stop all services:

```bash
./stop.sh
```

Or manually:
```bash
docker-compose down
```

## Understanding the Code

### ProducerExample

- Connects to RocketMQ NameServer at `localhost:9876`
- Sends 10 messages to `TestTopic`
- Each message has a tag `TagA` and a unique key
- Prints the message ID and queue ID after sending

### ConsumerExample

- Connects to RocketMQ NameServer at `localhost:9876`
- Subscribes to `TestTopic` with consumer group `TestConsumerGroup`
- Receives messages and prints them
- Acknowledges messages after processing
