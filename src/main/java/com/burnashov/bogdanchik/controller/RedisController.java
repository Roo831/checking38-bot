package com.burnashov.bogdanchik.controller;

import com.burnashov.bogdanchik.MyBot;
import com.burnashov.bogdanchik.service.MatchStorageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Redis хранилище совпадений", description = "Работа с совпадениями в Redis")
public class RedisController {

    private final MatchStorageService storageService;

    @GetMapping("/all")
    @ApiResponse(responseCode = "200", description = "Список всех записей")
    public List<RedisEntryDto> getAll() {
        return storageService.findAll();
    }
}