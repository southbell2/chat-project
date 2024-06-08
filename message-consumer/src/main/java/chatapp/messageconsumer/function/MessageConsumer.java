package chatapp.messageconsumer.function;

import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.service.ConsumerTaskService;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    @Bean
    public Consumer<List<ChatMessage>> consume(ConsumerTaskService service) {
        return chatMessageList -> {
            service.publishRedis(chatMessageList);
            service.saveMessageInCassandra(chatMessageList);
        };
    }

}
