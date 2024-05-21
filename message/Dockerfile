FROM bellsoft/liberica-openjdk-alpine:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} message.jar

COPY src/main/resources/redis.jks redis.jks

EXPOSE 8080

ENTRYPOINT ["java","-jar","/message.jar","--spring.profiles.active=aws"]