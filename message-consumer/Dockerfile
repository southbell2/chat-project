FROM bellsoft/liberica-openjdk-alpine:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} message-consumer.jar

COPY src/main/resources/cassandra_truststore.jks cassandra_truststore.jks

COPY src/main/resources/redis.jks redis.jks

EXPOSE 8081

ENTRYPOINT ["java","-Xms4g","-Xmx6g","-jar","/message-consumer.jar","--spring.profiles.active=aws"]