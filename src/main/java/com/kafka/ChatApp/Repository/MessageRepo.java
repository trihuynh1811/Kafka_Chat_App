package com.kafka.ChatApp.Repository;

import com.kafka.ChatApp.Entity.Conversation;
import com.kafka.ChatApp.Entity.Message_;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepo extends JpaRepository<Message_, Long> {
    List<Message_> findTop50ByTopicOrderByTimestampAsc(Conversation topic);
    List<Message_> findTop10ByTopicOrderByTimestampDesc(Conversation topic);

    List<Message_> findTop10ByTimestampLessThanAndTopicOrderByTimestampDesc(LocalDateTime timestamp, Conversation topicId);
    List<Message_> findTop10ByTimestampGreaterThanEqualAndTopicOrderByTimestampAsc(LocalDateTime timestamp, Conversation topicId);
    List<Message_> findTop10ByTimestampGreaterThanAndTopicOrderByTimestampAsc(LocalDateTime timestamp, Conversation topicId);

    Message_ findFirstByTopicOrderByTimestampDesc(Conversation topic);
}
