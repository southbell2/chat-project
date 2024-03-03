package chatapp.messageconsumer.function;

import chatapp.messageconsumer.IdGenerator;
import chatapp.messageconsumer.message.Bucket;
import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.message.MessageRepository;
import chatapp.messageconsumer.message.casssandra.Message;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConsumer {

    private final IdGenerator idGenerator = new IdGenerator();

    @Bean
    public Consumer<ChatMessage> consume(MessageRepository messageRepository) {
        return chatMessage -> {
            Message message = createMessage(chatMessage);
            messageRepository.save(message);
        };
    }

    private Message createMessage(ChatMessage chatMessage) {
        long messageId = idGenerator.nextId();
        int bucket = getBucket(messageId);
        return Message.createMessage(chatMessage.getChannelId(), bucket, messageId,
            chatMessage.getNickname(), chatMessage.getContent());
    }

    private int getBucket(long messageId) {
        long[] parse = idGenerator.parse(messageId);
        return Bucket.calculateBucket(parse[0]);
    }
}
