package com.burnashov.checking38.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InMemoryKeywordService implements KeywordService {

    private final Set<String> keywordSet;

    @Override
    public Set<String> getKeywords() {
        return Collections.unmodifiableSet(keywordSet);
    }

    @Override
    public boolean contains(String text) {
        return keywordSet.stream().anyMatch(text::contains);
    }

    @Override
    public Optional<String> findFirst(String text) {
        return keywordSet.stream().filter(text::contains).findFirst();
    }

    @Override
    public void add(String keyword) {
        keywordSet.add(keyword.toLowerCase().trim());
    }
}
