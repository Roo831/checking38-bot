package com.burnashov.bogdanchik.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RedisEntryDto(@Schema(description = "Ключ в Redis") String key, @Schema(description = "Значение в Redis") String value) {}