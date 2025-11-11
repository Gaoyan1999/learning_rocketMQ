# Learning RocketMQ - Roadmap

A comprehensive roadmap to master Apache RocketMQ, a distributed messaging and streaming platform.

## 📚 Learning Path

### Phase 1: Fundamentals - Core Concepts

- **Producer**: Message sender
- **Consumer**: Message receiver
- **Broker**: Message storage and forwarding server
- **NameServer**: Service discovery and routing
- **Topic**: Message category/queue
- **Tag**: Message subcategory for filtering
- **Message Queue**: Physical queue within a topic
- **Consumer Group**: Group of consumers sharing the same subscription

#### 1.3 RocketMQ Architecture
-  Cluster architecture (NameServer, Broker, Producer, Consumer)
-  Message storage model
-  Message routing mechanism
-  High availability design

**Resources:**
- Official documentation: https://rocketmq.apache.org/docs/quick-start/
- Architecture overview videos/articles

---

### Phase 2: Installation & Setup with Docker Compose

**Reference:** 

- [Official Docker Compose Guide](https://rocketmq.apache.org/docs/quickStart/03quickstartWithDockercompose)
- see `./phase2_guide.md`
---

### Phase 3: Basic Operations

#### 3.1 Producer Basics
-  Create a Producer instance
-  Send synchronous messages
-  Send asynchronous messages
-  Send one-way messages
-  Handle send results and exceptions
-  Message keys and properties

#### 3.2 Consumer Basics
-  Create a Consumer instance
-  Subscribe to topics
-  Push consumer (reactive)
-  Pull consumer (polling)
-  Message consumption patterns
-  Consumer tags filtering

#### 3.3 Message Types
-  Normal messages
-  Ordered messages
-  Scheduled/delayed messages
-  Batch messages
-  Transaction messages

**Hands-on Projects:**
1. Simple producer-consumer application
2. Order processing system (ordered messages)
3. Notification system (delayed messages)

---

### Phase 4: Advanced Features

#### 4.1 Message Ordering
-  Global ordering
-  Partition ordering
-  Use cases and best practices

#### 4.2 Transaction Messages
-  Two-phase commit in RocketMQ
-  Local transaction execution
-  Transaction status check
-  Implementing distributed transactions

#### 4.3 Message Filtering
-  Tag-based filtering
-  SQL92 filtering
-  Custom filtering

#### 4.4 Batch Operations
-  Batch message sending
-  Batch consumption
-  Performance optimization

#### 4.5 Message Retry & DLQ
-  Consumer retry mechanism
-  Retry queue
-  Dead Letter Queue (DLQ)
-  Retry policy configuration

**Hands-on Projects:**
1. E-commerce order system with transactions
2. Event-driven microservices architecture
3. Message filtering system

---

### Phase 5: Production Deployment

#### 5.1 Broker Configuration
-  Broker roles (Master/Slave)
-  Replication configuration
-  Storage configuration
-  Performance tuning

#### 5.2 Cluster Setup
-  NameServer cluster
-  Broker cluster (Master-Slave)
-  Multi-master deployment
-  High availability configuration

#### 5.3 Producer/Consumer Configuration
-  Connection pool settings
-  Timeout configurations
-  Thread pool configuration
-  Load balancing

#### 5.4 Monitoring & Operations
-  RocketMQ Console monitoring
-  Metrics collection
-  Log management
-  Alerting setup

**Hands-on:**
- Deploy a production-like cluster
- Set up monitoring and alerting

---

### Phase 6: Advanced Topics

#### 6.1 Message Storage
-  CommitLog structure
-  ConsumeQueue structure
-  IndexFile structure
-  Message persistence mechanism

#### 6.2 Performance Optimization
-  Producer performance tuning
-  Consumer performance tuning
-  Broker performance tuning
-  Network optimization
-  JVM tuning

#### 6.3 RocketMQ Streams
-  Stream processing concepts
-  Stream API usage
-  Window operations
-  Stateful processing

#### 6.4 RocketMQ Connect
-  Connector architecture
-  Source connectors
-  Sink connectors
-  Custom connectors

#### 6.5 Security
-  ACL (Access Control List)
-  Authentication
-  Encryption
-  Network security

**Hands-on Projects:**
1. High-performance message processing system
2. Real-time stream processing application
3. Data pipeline with connectors

---

### Phase 7: Best Practices & Patterns

#### 7.1 Design Patterns
-  Event sourcing
-  CQRS (Command Query Responsibility Segregation)
-  Saga pattern
-  Outbox pattern
-  Idempotency patterns

#### 7.2 Error Handling
-  Exception handling strategies
-  Circuit breaker pattern
-  Graceful degradation
-  Message replay strategies

#### 7.3 Testing
-  Unit testing producers/consumers
-  Integration testing
-  Performance testing
-  Chaos engineering

#### 7.4 Troubleshooting
-  Common issues and solutions
-  Debugging techniques
-  Performance bottleneck identification
-  Log analysis

**Hands-on:**
- Build a complete microservices system using RocketMQ
- Implement error handling and monitoring
- Write comprehensive tests

---

## 🛠️ Practical Projects

### Beginner Projects
1. **Simple Messaging App**: Producer sends messages, consumer receives them
2. **Order Notification System**: Send order confirmations with delayed messages
3. **Log Aggregation**: Collect logs from multiple services

### Intermediate Projects
1. **E-commerce Platform**: Order processing with transaction messages
2. **Event-Driven Architecture**: Microservices communication via events
3. **Notification Service**: Multi-channel notifications (email, SMS, push)

### Advanced Projects
1. **Real-time Analytics**: Stream processing for analytics
2. **Distributed System**: Complete microservices architecture
3. **Data Pipeline**: ETL pipeline using RocketMQ

---

## 📖 Recommended Resources

### Official Documentation
- [RocketMQ Official Docs](https://rocketmq.apache.org/docs/)
- [RocketMQ GitHub](https://github.com/apache/rocketmq)
- [RocketMQ Examples](https://github.com/apache/rocketmq/tree/master/example)

### Books
- "RocketMQ Technology Insider" (if available)
- "Distributed Systems" concepts

### Online Courses & Tutorials
- RocketMQ official tutorials
- YouTube tutorials
- Technical blogs

### Community
- [RocketMQ Mailing Lists](https://rocketmq.apache.org/community/)
- Stack Overflow (rocketmq tag)
- GitHub Issues and Discussions

---

## 🎯 Learning Checklist

### Core Concepts
-  Understand message queue fundamentals
-  Master RocketMQ architecture
-  Know all core components

### Practical Skills
-  Can set up RocketMQ locally
-  Can write producers and consumers
-  Can deploy a cluster
-  Can monitor and troubleshoot

### Advanced Skills
-  Can optimize performance
-  Can design event-driven systems
-  Can implement distributed transactions
-  Can handle production issues

---

## 💡 Tips for Learning

1. **Practice Regularly**: Code along with tutorials, don't just read
2. **Build Projects**: Apply concepts in real projects
3. **Read Source Code**: Understanding the implementation deepens knowledge
4. **Join Community**: Ask questions and share knowledge
5. **Experiment**: Try different configurations and see what happens
6. **Document Your Learning**: Keep notes on what you learn

---

## 🚀 Next Steps

1. Start with Phase 1 and work through systematically
2. Set up your development environment
3. Create your first producer and consumer
4. Build progressively more complex projects
5. Contribute to open source or share your learnings

---

**Happy Learning! 🎉**
