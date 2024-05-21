FROM bellsoft/liberica-openjdk-alpine:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} message.jar

COPY src/main/resources/redis.jks redis.jks

EXPOSE 8080

ENTRYPOINT ["java", \
 "-javaagent:/pinpoint-agent/pinpoint-bootstrap-2.5.3.jar", \
 "-Dpinpoint.agentId=message-1", \
 "-Dpinpoint.applicationName=message", \
 "-jar","/message.jar","--spring.profiles.active=aws"]