package com.rosoa0475.websocketchat.domain;

import com.rosoa0475.websocketchat.dto.ChatMessageDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String oauth2Id;
    private String message;
    private Long chatRoomId;
    private String sender;
    public ChatMessage(String oauth2Id, String message, Long chatRoomId, String sender) {
        this.oauth2Id = oauth2Id;
        this.message = message;
        this.chatRoomId = chatRoomId;
        this.sender = sender;
    }

    public ChatMessageDto toChatMessageDto() {
        return new ChatMessageDto(message, chatRoomId, oauth2Id,sender);
    }
}
