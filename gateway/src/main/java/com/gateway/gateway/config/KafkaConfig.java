package com.gateway.gateway.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
@RestController
public class KafkaConfig {
    @Value(value = "${kafka.broker}") String kafkaBroker;

    @GetMapping("/howManyPartitions")
	public String test() throws InterruptedException, ExecutionException {
		Map<String, Object> configs = Collections.singletonMap("bootstrap.servers", kafkaBroker);
        AdminClient adminClient = AdminClient.create(configs);

        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList("email"));
        Map<String, TopicDescription> topicDescriptionMap = describeTopicsResult.all().get();
        
        TopicDescription topicDescription = topicDescriptionMap.get("email");
        int numberOfPartitions = topicDescription.partitions().size();
        
        System.out.println("Number of partitions: " + numberOfPartitions);
		return "Number of partitions: " + numberOfPartitions;
	}

    // Add more NewTopic beans for additional topics
}