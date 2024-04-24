FROM bellsoft/liberica-openjdk-alpine:17

CMD ["./gradlew", "clean", "build"]

VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} chat-app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/chat-app.jar"]