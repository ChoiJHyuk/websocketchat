package com.rosoa0475.websocketchat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;

    public void putToken(String roomId, String oauth2Id, String token){
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(roomId,oauth2Id,token);
    }

    public Map<String, String> getAllDisconnectedUser(String roomId){
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> entries = hashOperations.entries(roomId);
        return entries;
    }

    public void deleteToken(String roomId,String oauth2Id){
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(roomId,oauth2Id);
    }

}
