package com.burnashov.checking38.controller;

import com.burnashov.checking38.service.MatchStorageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burnashov.checking38.dto.RedisEntryDto;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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