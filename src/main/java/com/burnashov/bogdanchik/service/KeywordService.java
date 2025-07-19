package com.burnashov.bogdanchik.service;

import java.util.Optional;
import java.util.Set;

public interface KeywordService {
    Set<String> getKeywords();
    boolean contains(String text);
    Optional<String> findFirst(String text);
    void add(String keyword);
}