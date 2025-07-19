package com.burnashov.bogdanchik.controller;

import com.burnashov.bogdanchik.MyBot;
import com.burnashov.bogdanchik.service.MatchStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final MatchStorageService storageService;

    @GetMapping("/all")
    public List<RedisEntryDto> getAll() {
        return storageService.findAll();
    }
}