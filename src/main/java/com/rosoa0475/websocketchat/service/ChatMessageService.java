package com.rosoa0475.websocketchat.service;

import com.rosoa0475.websocketchat.domain.ChatMessage;
import com.rosoa0475.websocketchat.dto.ChatMessageDto;
import com.rosoa0475.websocketchat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDto> getAllUnreadMessage(String oauth2Id){
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByOauth2Id(oauth2Id);
        List<ChatMessageDto> chatMessageDtos = chatMessages.stream().map(ChatMessage::toChatMessageDto).collect(Collectors.toList());
        return chatMessageDtos;
    }

    public void save(String oauth2id, String message, Long roomId, String sender){
        ChatMessage chatMessage = new ChatMessage(oauth2id, message, roomId, sender);
        chatMessageRepository.save(chatMessage);
    }

    public void deleteAll(String oauth2Id){
        chatMessageRepository.deleteAllByOauth2Id(oauth2Id);
    }
}
