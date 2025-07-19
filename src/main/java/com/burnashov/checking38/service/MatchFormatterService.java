package com.burnashov.checking38.service;

import org.springframework.stereotype.Service;

@Service
public class MatchFormatterService {

    public String formatForRedis(String chatTitle, Integer messageId, Long chatId, String keyword, String username, String text) {
        return String.format("Chat: %s; Message ID: %d; Chat ID: %d; Keyword: %s; User: @%s; Text: %s",
                chatTitle, messageId, chatId, keyword, username, text);
    }

    public String formatForAdmin(String chatTitle, Integer messageId, Long chatId, String keyword, String username, String text) {
        return String.format("""
                Совпадение!
                Chat: %s
                Message ID: %d
                Chat ID: %d
                Keyword: %s
                User: @%s
                Text: %s
                """, chatTitle, messageId, chatId, keyword, username, text);
    }
}