package demo.message.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
        @Value("${spring.data.redis.host}") String host,
        @Value("${spring.data.redis.port}") int port,
        @Value("${spring.data.ssl:false}") boolean sslEnabled) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);

        LettuceClientConfigurationBuilder clientConfigurationBuilder = LettuceClientConfiguration.builder();

        if (sslEnabled) {
            SslOptions sslOptions = SslOptions.builder()
                .truststore(new File("/redis.jks"), "asdf1234")
                .build();

            ClientOptions clientOptions = ClientOptions
                .builder()
                .sslOptions(sslOptions)
                .protocolVersion(ProtocolVersion.RESP3)
                .build();

            clientConfigurationBuilder
                .clientOptions(clientOptions)
                .useSsl();
        }

        return new LettuceConnectionFactory(redisStandaloneConfiguration,
            clientConfigurationBuilder.build());
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
