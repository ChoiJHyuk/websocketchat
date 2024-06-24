package com.rosoa0475.websocketchat.repository;

import com.rosoa0475.websocketchat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    public List<ChatMessage> findAllByOauth2Id(String oauth2Id);
    public void deleteAllByOauth2Id(String oauth2Id);
}
