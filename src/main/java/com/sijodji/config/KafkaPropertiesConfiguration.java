package com.sijodji.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;

@Data
@NoArgsConstructor
public class KafkaPropertiesConfiguration {

    private Map<String, KafkaProperties.Consumer> consumers;
    private Map<String, KafkaProperties.Producer> producers;
}
