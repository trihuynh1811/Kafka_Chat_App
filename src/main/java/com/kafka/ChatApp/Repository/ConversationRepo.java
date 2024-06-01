package com.kafka.ChatApp.Repository;

import com.kafka.ChatApp.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepo extends JpaRepository<Conversation, Long> {
}
