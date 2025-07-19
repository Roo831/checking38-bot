package com.burnashov.bogdanchik;

import com.burnashov.bogdanchik.repository.MatchStorage;
import com.burnashov.bogdanchik.service.ExcelProcessingService;
import com.burnashov.bogdanchik.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.InputStream;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {

    private final KeywordService keywordService;
    private final ExcelProcessingService excelProcessingService;
    private final MatchStorage matchStorage;

    @Value("${telegrambots.bots.username}")
    private String botUsername;

    @Value("${telegrambots.bots.token}")
    private String botToken;

    @Value("${telegrambots.bots.admin.chat.id}")
    private Long adminChatId;

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Получаем обновление: {}", update);

        try {
            if (update.hasMessage()) {
                handleMessage(update.getMessage());
            }

            if (update.hasChannelPost()) {
                handleMessage(update.getChannelPost());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки update", e);
            sendToAdmin("Ошибка при обработке update: " + e.getMessage());
        }
    }

    private void handleMessage(Message message) {
        if (message.hasDocument() && isAdmin(message)) {
            handleExcel(message);
        }

        if (message.hasText()) {
            processTextMessage(message);
        }
    }

    private boolean isAdmin(Message message) {
        return message.getFrom() != null && message.getFrom().getId().equals(adminChatId);
    }

    private void handleExcel(Message message) {
        String fileName = message.getDocument().getFileName();

        if (!fileName.endsWith(".xlsx")) {
            sendToAdmin("Отправьте .xlsx файл");
            return;
        }

        try {
            GetFile getFile = new GetFile(message.getDocument().getFileId());
            File file = execute(getFile);
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

            try (InputStream inputStream = new URL(fileUrl).openStream()) {
                excelProcessingService.process(inputStream);
                sendToAdmin("Ключевые слова загружены. Всего: " + keywordService.getKeywords().size());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке Excel-файла", e);
            sendToAdmin("Ошибка загрузки Excel-файла: " + e.getMessage());
        }
    }

    private void processTextMessage(Message message) {
        String text = message.getText().toLowerCase();
        String chatTitle = message.getChat().getTitle() != null ? message.getChat().getTitle() : "private_chat";

        keywordService.findFirst(text).ifPresent(keyword -> {
            String redisKey = "%s:%s:%d".formatted(
                    message.getChatId(),
                    message.getFrom() != null ? message.getFrom().getUserName() : "unknown_user",
                    message.getMessageId()
            );

            String redisValue = """
                    Chat: %s
                    Message ID: %d
                    Chat ID: %d
                    Keyword: %s
                    User: @%s
                    Text: %s
                    """.formatted(
                    chatTitle,
                    message.getMessageId(),
                    message.getChatId(),
                    keyword,
                    message.getFrom() != null ? message.getFrom().getUserName() : "unknown",
                    text
            );

            matchStorage.saveMatch(redisKey, redisValue);
            sendToAdmin("Совпадение!\n" + redisValue);
        });
    }

    private void sendToAdmin(String text) {
        SendMessage message = new SendMessage(adminChatId.toString(), text);
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения администратору", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}