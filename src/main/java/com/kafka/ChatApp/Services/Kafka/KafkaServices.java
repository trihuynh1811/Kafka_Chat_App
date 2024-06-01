package com.kafka.ChatApp.Services.Kafka;

import org.apache.kafka.clients.admin.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaServices {
    @Autowired
    private AdminClient adminClient;
    private final KafkaAdmin kafkaAdmin;

    @Autowired
    public KafkaServices(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    //Get all the group in kafka
    public List<ConsumerGroupListing> getConsumerGroups() throws ExecutionException, InterruptedException {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();
            return listConsumerGroupsResult.all().get().stream().toList();
        }
    }

    public List<String> getTopicList() throws ExecutionException, InterruptedException {
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true); // Include internal topics if needed

        ListTopicsResult topicsResult = adminClient.listTopics(options);
        Set<String> topics = topicsResult.names().get();
        return topics.stream().toList();
    }

    public void createTopic(String topicName, int numOfPartition, short replicationFactor){
        NewTopic topic = new NewTopic(topicName, numOfPartition, replicationFactor);
        CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(topic));
        System.out.println(result);
    }

    public void deleteTopic(String topicName){
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singletonList(topicName));
        System.out.println(result);
    }


}
