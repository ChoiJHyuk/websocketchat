package com.rosoa0475.websocketchat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosoa0475.websocketchat.dto.ChatMessageDto;
import com.rosoa0475.websocketchat.service.ChatMessageService;
import com.rosoa0475.websocketchat.service.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class ChatHandler extends TextWebSocketHandler {
    //각 방마다 현재 연결되어 있는 session 목록
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap;
    private final RedisService redisService;
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long chatRoomId = Long.parseLong(session.getUri().getQuery().split("&")[0].split("=")[1]);
        String oauth2Id = session.getUri().getQuery().split("&")[1].split("=")[1];
        if (!chatRoomSessionMap.containsKey(chatRoomId)) {
            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
        } else {
            redisService.deleteToken(chatRoomId.toString(), oauth2Id);
            List<ChatMessageDto> chatMessageDtos = chatMessageService.getAllUnreadMessage(oauth2Id);
            chatMessageService.deleteAll(oauth2Id);
            for (ChatMessageDto chatMessageDto : chatMessageDtos) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
            }
        }
        chatRoomSessionMap.get(chatRoomId).add(session);
    }

    @Override
    @Async // websocket은 계속해서 데이터를 주고 받으므로 비동기적으로 처리하여 서버 처리량을 높이고 응답시간을 단축
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload();
        Long chatRoomId = Long.parseLong(session.getUri().getQuery().split("&")[0].split("=")[1]);
        String oauth2Id = session.getUri().getQuery().split("&")[1].split("=")[1];
        Set<WebSocketSession> roomSessions = chatRoomSessionMap.get(chatRoomId);
        for (WebSocketSession roomSession : roomSessions) {
            String oauthId = roomSession.getUri().getQuery().split("&")[1].split("=")[1];
            ChatMessageDto chatMessageDto = new ChatMessageDto(msg, chatRoomId, oauthId, oauth2Id);
            /*
                rabbitTemplate.convertAndSend("chat.exchange","room."+chatRoomId.toString(), chatMessageDto);
                rabbitmq로 메세지 전송. front에서 메세지 받기
             */
            roomSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
        }
        /*
            fcm토큰으로 알림 보내기
        */
        for (Map.Entry<String, String> entry : redisService.getAllDisconnectedUser(chatRoomId.toString()).entrySet()) {
            String oauthId = entry.getKey();
            chatMessageService.save(oauthId, msg, chatRoomId, oauth2Id);
        }
    }

    @Override
    @Transactional
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long chatRoomId = Long.parseLong(session.getUri().getQuery().split("&")[0].split("=")[1]);
        String oauth2Id = session.getUri().getQuery().split("&")[1].split("=")[1];
        chatRoomSessionMap.get(chatRoomId).remove(session);
        chatMessageService.deleteAll(oauth2Id);
        redisService.putToken(chatRoomId.toString(), oauth2Id, "1123"); // fcm token 넣어줘야함
    }
}
