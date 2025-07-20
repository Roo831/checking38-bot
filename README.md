# CHECK38 Telegram Bot

Этот проект представляет собой Telegram-бота для мониторинга сообщений на основе ключевых слов.  
Бот реагирует на текст, загружает `.xlsx` с ключевыми словами, сохраняет совпадения в Redis и предоставляет API для управления.

---

## Функциональность

- Обработка текстовых сообщений от пользователей.
- Загрузка ключевых слов из `.xlsx` файла администратором.
- Поиск совпадений по ключевым словам (case-insensitive).
- Хранение совпадений в Redis с ключами вида:
  ```
  chatId:username:messageId
  ```
- Команда Telegram `/username <имя>` — администратор получает все совпадения от пользователя.
- REST API для просмотра всех совпадений и ключей.
- Swagger-документация для REST API.

---

## Быстрый старт с Docker Compose

### 1. Создайте `.env` файл в корне:

```env
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_ADMIN_CHAT_ID=your_admin_chat_id1 your_admin_chat_id2 your_admin_chat_id3 your_admin_chat_id4
```

### 2. Соберите проект:

```bash
./gradlew clean build
```

### 3. Запустите:

```bash
docker-compose up --build
```

---

## API Документация

- Swagger доступен по адресу:  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Примеры:

- `GET /redis/all` — получить все записи из Redis.
- `GET /checkSet` — проверить список ключевых слов в памяти.

---

## ⚙Переменные окружения

| Переменная               | Описание                                                   |
|--------------------------|------------------------------------------------------------|
| `TELEGRAM_BOT_TOKEN`     | Токен Telegram-бота                                        |
| `TELEGRAM_ADMIN_CHAT_ID`| Перечисление через пробел ID Telegram-чата администраторов |
| `SPRING_REDIS_HOST`      | Redis хост (по умолчанию `redis`)                          |
| `SPRING_REDIS_PORT`      | Redis порт (по умолчанию `6379`)                           |
| `SPRING_REDIS_PASSWORD`  | Redis пароль (по умолчанию `201999`)                       |

---

## Структура проекта

```
bogdanchik/
├── config/                # Конфигурации Spring и Telegram API
├── controller/            # REST-контроллеры
├── dto/                   # DTO для Redis
├── repository/            # Работа с Redis
├── service/               # Логика: ключевые слова, сохранение, Excel
├── MyBot.java             # Класс Telegram-бота
├── BogdanchikApplication  # Точка входа
└── resources/
    └── application.yml    # Настройки Spring
```