package com.burnashov.bogdanchik.repository;

import com.burnashov.bogdanchik.dto.RedisEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MatchRepository {

    private final StringRedisTemplate redisTemplate;

    public void saveMatch(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("Сохранено в Redis: Ключ - {}. Значение - {}", key, value);
    }

    public List<RedisEntryDto> findAll() {
        Set<String> keys = redisTemplate.keys("*");
        List<RedisEntryDto> result = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                result.add(new RedisEntryDto(key, value));
            }
        }
        return result;
    }

    public List<RedisEntryDto> findByUsername(String username) {
        Set<String> keys = redisTemplate.keys("*:*" + username + "*:*");
        List<RedisEntryDto> result = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                if (key.contains(":" + username + ":")) {
                    String value = redisTemplate.opsForValue().get(key);
                    result.add(new RedisEntryDto(key, value));
                }
            }
        }
        return result;
    }
}