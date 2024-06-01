package com.kafka.ChatApp.Repository;

import com.kafka.ChatApp.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Contact, String> {

    Contact findByEmail(String email);

    Optional<Contact> findByDisplayName(String displayName);
}
