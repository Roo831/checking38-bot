package com.burnashov.checking38;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@SpringBootApplication
@Slf4j
public class CheckingBotApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(CheckingBotApplication.class, args);

        TelegramBotsApi botsApi = ctx.getBean(TelegramBotsApi.class);
        MyBot bot = ctx.getBean(MyBot.class);

        try {
            botsApi.registerBot(bot);
            System.out.println("Bot registered successfully.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
    }
}