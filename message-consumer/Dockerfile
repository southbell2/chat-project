FROM bellsoft/liberica-openjdk-alpine:17

VOLUME /tmp

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} message-consumer.jar

COPY src/main/resources/cassandra_truststore.jks cassandra_truststore.jks

COPY src/main/resources/redis.jks redis.jks

EXPOSE 8080

ENTRYPOINT ["java","-jar","/message-consumer.jar","--spring.profiles.active=aws"]