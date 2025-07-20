package com.burnashov.checking38;

import com.burnashov.checking38.repository.MatchRepository;
import com.burnashov.checking38.service.ExcelProcessingService;
import com.burnashov.checking38.service.KeywordService;
import com.burnashov.checking38.service.MatchFormatterService;
import com.burnashov.checking38.service.MatchStorageService;
import jakarta.annotation.PostConstruct;
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
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyBot extends TelegramLongPollingBot {

    private final KeywordService keywordService;
    private final ExcelProcessingService excelProcessingService;
    private final MatchRepository matchRepository;
    private final MatchFormatterService formatterService;
    private final MatchStorageService storageService;

    @Value("${telegrambots.bots.username}")
    private String botUsername;

    @Value("${telegrambots.bots.token}")
    private String botToken;

    @Value("${telegrambots.bots.admin.chat.id}")
    private String adminChatIdsString;

    private final Set<Long> adminChatIds = new HashSet<>();

    @PostConstruct
    public void executeAdminsId() {
        String[] ids = adminChatIdsString.split("\\s+");
        for (String id : ids) {
            adminChatIds.add(Long.parseLong(id));
        }
    }

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

            String text = message.getText().trim();
            if (text.startsWith("/username ") && isAdmin(message)) {
                String username = text.substring("/username ".length()).trim();
                sendToAdmin(storageService.getMatchesByName(username));
                return;
            }
            processTextMessage(message);
        }
    }

    private boolean isAdmin(Message message) {
        for (Long id : adminChatIds) {

            if (message.getFrom() != null && message.getFrom().getId().equals(id)) {
                return true;
            }
        }
        return false;
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
        String username = Optional.ofNullable(message.getFrom())
                .map(User::getUserName)
                .orElse("unknown_user");
        ;

        keywordService.findFirst(text).ifPresent(keyword -> {
            String redisKey = "%s:%s:%d".formatted(
                    message.getChatId(),
                    message.getFrom() != null ? message.getFrom().getUserName() : "unknown_user",
                    message.getMessageId()
            );

            String redisValue = formatterService.formatForRedis(chatTitle, message.getMessageId(), message.getChatId(), keyword, username, text);
            String notification = formatterService.formatForAdmin(chatTitle, message.getMessageId(), message.getChatId(), keyword, username, text);

            matchRepository.saveMatch(redisKey, redisValue);
            sendToAdmin(notification);
        });
    }

    private void sendToAdmin(String text) {

        try {
            for (Long id : adminChatIds) {
                SendMessage message = new SendMessage(id.toString(), text);
                execute(message);
            }

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