package chatapp.messageconsumer.function;

import chatapp.messageconsumer.message.ChatMessage;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConsumer {

    @Bean
    public Consumer<ChatMessage> consume() {
        return chatMessage -> {
            System.out.println("ChatMessage = " + chatMessage);
        };
    }
}
