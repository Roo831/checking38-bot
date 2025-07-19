package com.burnashov.bogdanchik.controller;

import com.burnashov.bogdanchik.service.InMemoryKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/checkSet")
@RequiredArgsConstructor
public class CheckSetController {

     private final InMemoryKeywordService keywordService;

    @GetMapping
    public String checkSet() {
        Set<String> keywords = keywordService.getKeywords();
        if (keywords.isEmpty()) {
            return "Сет пустой. Всего ключевых слов: 0";
        }
        return "Всего ключевых слов: " + keywords.size() + "\n" +
                String.join(", ", keywords);
    }
}
