package com.notification.notification.email.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${kafka.host}") String kafkaHost;
    @Bean
    public ConsumerFactory<String, String> consumerFactoryString() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost+":9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "email_senders");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // large traffic
        // config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        // config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        // config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        // config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> ListenerContainerFactoryString() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        // factory.setConcurrency(50); // Allowed number of threads for the consumer
        // factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.BATCH);
        factory.setConsumerFactory(consumerFactoryString());
        return factory;
    }
}
