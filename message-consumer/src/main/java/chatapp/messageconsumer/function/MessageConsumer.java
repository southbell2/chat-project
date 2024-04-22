package chatapp.messageconsumer.function;

import chatapp.messageconsumer.id.Bucket;
import chatapp.messageconsumer.id.generator.IdGenerator;
import chatapp.messageconsumer.id.manager.IdGeneratorManager;
import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.message.casssandra.Message;
import chatapp.messageconsumer.service.ConsumerTaskService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final IdGeneratorManager idGeneratorManager;

    @Bean
    public Consumer<ChatMessage> consume(ConsumerTaskService service) {
        return chatMessage -> {
            service.publishRedis(chatMessage);
            service.saveMessageInCassandra(createMessage(chatMessage));
            log.info("Message Consuming = {}", chatMessage);
        };
    }

    private Message createMessage(ChatMessage chatMessage) {
        IdGenerator idGenerator = idGeneratorManager.getIdGenerator();
        long messageId = idGenerator.nextId();
        int bucket = getBucket(messageId, idGenerator);
        return Message.createMessage(chatMessage.getChannelId(), bucket, messageId,
            chatMessage.getNickname(), chatMessage.getContent());
    }

    private int getBucket(long messageId, IdGenerator idGenerator) {
        long[] parse = idGenerator.parse(messageId);
        return Bucket.calculateBucket(parse[0]);
    }
}
