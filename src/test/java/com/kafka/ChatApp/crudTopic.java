package com.kafka.ChatApp;

import com.kafka.ChatApp.Services.Kafka.KafkaServices;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
@EnableKafka
public class crudTopic {



    @Bean
    public Map<String, Object> producerConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configurations.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configurations.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configurations;
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(producerConfigurations());
    }

    @Test
    public void deleteTopic() throws ExecutionException, InterruptedException {
        DeleteTopicsResult result = adminClient().deleteTopics(Collections.singletonList("group-1"));

        while (!result.all().isDone()){
            System.out.println("deleting topic");
        }
    }

    @Test
    public void getTopicList() throws ExecutionException, InterruptedException {
        System.out.println(adminClient().listTopics().names().get().stream().toList());
        System.out.println(adminClient().listTopics().names().get().stream().toList().size());
    }

    @Test
    public void createTopic() throws ExecutionException, InterruptedException {
        CreateTopicsResult result = adminClient().createTopics(Collections.singletonList(new NewTopic("group-3", 1, (short) 1)));

        KafkaFuture<Void> future = result.values().get("group-3");
        future.get();
        System.out.println(result);
    }
}
