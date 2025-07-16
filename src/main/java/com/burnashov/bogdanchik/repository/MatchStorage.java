package com.burnashov.bogdanchik.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MatchStorage {

    private final StringRedisTemplate redisTemplate;

    public void saveMatch(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("Сохранено в Базу Данных: Ключ - {}. Значение - {}", key, value);
    }
}