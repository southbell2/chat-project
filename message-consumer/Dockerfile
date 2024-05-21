FROM bellsoft/liberica-openjdk-alpine:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} message-consumer.jar

COPY src/main/resources/cassandra_truststore.jks cassandra_truststore.jks

COPY src/main/resources/redis.jks redis.jks

EXPOSE 8081

ENTRYPOINT ["java", \
 "-javaagent:/pinpoint-agent/pinpoint-bootstrap-2.5.3.jar", \
 "-Dpinpoint.agentId=message-consumer-1", \
 "-Dpinpoint.applicationName=message-consumer", \
 "-jar","/message-consumer.jar","--spring.profiles.active=aws"]