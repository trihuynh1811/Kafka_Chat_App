package com.kafka.ChatApp.Repository;

import com.kafka.ChatApp.Entity.Contact;
import com.kafka.ChatApp.Entity.Conversation;
import com.kafka.ChatApp.Entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMemberRepo extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByContact(Contact username);
}
