# Kafka producers/consumers property-based generation

Create beans `KafkaTemplate<String, ? extends BasicEvent>` and `KafkaMessageListenerContainer` for every event
specified in properties file.

Producer usage: inject bean `BasicEventKafkaSender<T extends BasicEvent>` (create automatically
from properties) into your class.<br />
Consumer usage: create bean in context by extending `EventReceiver<T extends BasicEvent>` and override
mandatory method for receiving from kafka topic (will be invoked automatically by `KafkaMessageListenerContainer`).

All events must be data classes extend from `BasicEvent` and represented in `EventType` enum. Event type in properties
file should be in lowel-hypen string style and suit to upper underscore style of enum constant in `EventType`

Properties file example:
```yaml
events:
  some-event:
    producer: default
    consumer: some-event-consumer
  another-event:
    producer: default
    consumer: another-event-consumer
  configs:
    producers:
      default:
        bootstrap-servers: ${spring.embedded.kafka.brokers}
    consumers:
      some-event-consumer:
        bootstrap-servers: ${spring.embedded.kafka.brokers}
        group-id: some-event-consumer-group
        auto-startup: true
        auto-offset-reset: earliest
      another-event-consumer:
        bootstrap-servers: ${spring.embedded.kafka.brokers}
        group-id: another-event-consumer-group
        auto-startup: true
        auto-offset-reset: earliest
```
