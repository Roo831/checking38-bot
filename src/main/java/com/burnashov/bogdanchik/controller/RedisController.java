package com.burnashov.bogdanchik.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burnashov.bogdanchik.dto.RedisEntryDto;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final StringRedisTemplate redisTemplate;

    @GetMapping("/all")
    public List<RedisEntryDto> getAllKeysAndValues() {
        List<RedisEntryDto> result = new ArrayList<>();

        Set<String> keys = redisTemplate.keys("*");

        if (keys != null) {
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                result.add(new RedisEntryDto(key, value));
            }
        }
        return result;
    }
}