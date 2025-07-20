package com.burnashov.checking38.service;

import com.burnashov.checking38.dto.RedisEntryDto;
import com.burnashov.checking38.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchStorageService {

    private final MatchRepository matchRepository;

    public List<RedisEntryDto> findAll() {
        return matchRepository.findAll();
    }

    public List<RedisEntryDto> findByUsername(String username) {
        return matchRepository.findByUsername(username);
    }

    public String getMatchesByName(String username) {
        List<RedisEntryDto> matches = findByUsername(username);
        StringBuilder message = new StringBuilder("Найденные записи для пользователя @" + username + ":\n\n");
        for (RedisEntryDto dto : matches) {
            message.append(dto.value()).append("\n\n");
        }

        return message.toString();
    }
}