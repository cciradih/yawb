FROM maven:3-eclipse-temurin-17-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine
COPY --from=build /target/*.jar bot.jar
EXPOSE 25700
ENTRYPOINT ["java", "-jar", "bot.jar"]
