package com.user_management.user_management.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.host}") String kafkaHost;
    @Bean
    public ProducerFactory<String, String> producerFactoryString() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost+":9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateString() {
        return new KafkaTemplate<>(producerFactoryString());
    }



    // @AllArgsConstructor
    // @Data
    // class Greeting {
    //     String msg1;
    //     String msg2;
    // }
 
    // @Bean
    // public ProducerFactory<String, Greeting> producerFactoryGreeting() {
    //     Map<String, Object> config = new HashMap<>();

    //     config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    //     config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    //     return new DefaultKafkaProducerFactory<>(config);
    // }

    // @Bean
    // public KafkaTemplate<String, Greeting> kafkaTemplateGreeting() {
    //     return new KafkaTemplate<>(producerFactoryGreeting());
    // }

    // @Bean
    // public ProducerFactory<String, Object> producerFactoryObject() {
    //     Map<String, Object> config = new HashMap<>();

    //     config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    //     config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    //     return new DefaultKafkaProducerFactory<>(config);
    // }

    // @Bean
    // public KafkaTemplate<String, Object> kafkaTemplateObject() {
    //     return new KafkaTemplate<>(producerFactoryObject());
    // }
}
