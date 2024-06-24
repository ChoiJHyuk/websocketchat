package com.rosoa0475.websocketchat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class UtilConfig {
    @Bean
    public Map<Long, Set<WebSocketSession>> chatRoomSessionMap() {
        return new HashMap<>();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
