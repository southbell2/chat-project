package demo.chatapp.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedisConfig {

    private final String host;
    private final int port;

    public RedisClient client;
    public StatefulRedisConnection<String, String> connection;

    public RedisConfig(@Value("${redis.host}") String host, @Value("${redis.port}") int port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public StatefulRedisConnection<String, String> makeConnection() {
        RedisURI redisURI = RedisURI.Builder
            .redis(host, port)
            .build();

        client = RedisClient.create(redisURI);
        connection = client.connect();
        return connection;
    }

    @RequiredArgsConstructor
    static class RedisCommand {
        private final StatefulRedisConnection<String, String> connection;

        @Bean
        public RedisCommands<String, String> getSyncCommand() {
            return connection.sync();
        }

        @Bean
        public RedisAsyncCommands<String, String> getAsyncCommand() {
            return connection.async();
        }
    }

    @PreDestroy
    public void closeConnection() {
        connection.close();
        client.shutdown();
        log.info("---Redis Connection closed Successfully---");
    }
}
