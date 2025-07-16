package com.burnashov.bogdanchik;

import com.burnashov.bogdanchik.repository.MatchStorage;
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

@RequiredArgsConstructor
@Slf4j
@Component
public class MyBot extends TelegramLongPollingBot {

    private final KeywordLoader keywordLoader;
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
                Message message = update.getMessage();
                log.info("Обработка сообщения...");
                // Обработка Excel файла только от админа
                if (message.hasDocument()) {
                    log.info("Обработка документа...");
                    if (message.getFrom().getId().equals(adminChatId)) {
                        String fileName = message.getDocument().getFileName();
                        if (fileName.endsWith(".xlsx")) {
                            log.info("Обработка документа: {}", fileName);
                            handleExcelFile(message.getDocument().getFileId());
                        }
                        return;
                    } else log.error("Документ может загружать только администратор!");
                }
                processMessageForKeywords(message);
            }

            if (update.hasChannelPost()) {
                processMessageForKeywords(update.getChannelPost());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщения", e);
            sendToAdmin("Ошибка при обработке сообщения: " + e.getMessage());
        }
    }

    private void processMessageForKeywords(Message message) {
        if (message == null || message.getText() == null) return;

        String text = message.getText().toLowerCase();
        String chatTitle = message.getChat().getTitle();
        if (chatTitle == null) chatTitle = "private_chat";

        String finalChatTitle = chatTitle;
        keywordLoader.getAll().stream()
                .filter(text::contains)
                .findFirst()
                .ifPresent(keyword -> {

                    String formatString = "\nЧат: %s.\n Message ID: %d.\n Chat ID: %d.\n Ключевое слово: %s.\n Пользователь: @%s.\n Полный текст сообщения: %s.";

                    String redisKey = message.getChatId() + ":" + message.getFrom().getUserName() + ":" + message.getMessageId();
                    String redisValue = String.format(formatString.replaceAll("\n", ""),
                            finalChatTitle, message.getMessageId(), message.getChatId(), keyword, message.getFrom().getUserName(), message.getText());

                    matchStorage.saveMatch(redisKey, redisValue);

                    String notification = String.format("Найдено совпадение." + formatString,
                            finalChatTitle, message.getMessageId(), message.getChatId(), keyword, message.getFrom().getUserName(), message.getText());
                    sendToAdmin(notification);
                });
    }

    private void handleExcelFile(String fileId) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            File file = execute(getFile);
            String filePath = file.getFilePath();
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
            try (InputStream is = new URL(fileUrl).openStream()) {
                keywordLoader.loadFromExcel(is);
                sendToAdmin("Excel-файл обработан, ключевые слова загружены. Всего ключей: " + keywordLoader.getAll().size());
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки Excel-файла", e);
            sendToAdmin("Не удалось обработать файл: " + e.getMessage());
        }
    }

    private void sendToAdmin(String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(adminChatId.toString());
        msg.setText(text);
        try {
            execute(msg);
            log.info("Отправлено сообщение администратору: {}", text);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения админу", e);
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