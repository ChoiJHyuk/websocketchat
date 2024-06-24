package com.rosoa0475.websocketchat.dto;

import lombok.Getter;

@Getter
public class ChatMessageDto {
    private String message;
    private Long chatRoomId;
    private String oauth2Id;
    private String sender;
    public ChatMessageDto(String message, Long chatRoomId, String oauth2Id, String sender) {
        this.message = message;
        this.chatRoomId = chatRoomId;
        this.oauth2Id = oauth2Id;
        this.sender = sender;
    }
}
