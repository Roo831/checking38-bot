FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/checking38-bot-0.0.1-SNAPSHOT.jar bot.jar
ENTRYPOINT ["java", "-jar", "bot.jar"]