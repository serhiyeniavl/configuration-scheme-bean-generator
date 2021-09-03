package com.sijodji;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class BeanGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeanGeneratorApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
