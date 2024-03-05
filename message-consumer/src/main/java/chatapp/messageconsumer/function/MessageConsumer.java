package chatapp.messageconsumer.function;

import static chatapp.messageconsumer.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import chatapp.messageconsumer.id.Bucket;
import chatapp.messageconsumer.id.IdGenerator;
import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.message.MessageRepository;
import chatapp.messageconsumer.message.casssandra.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Slf4j
public class MessageConsumer {

    private final IdGenerator idGenerator = new IdGenerator();

    @Bean
    public Consumer<ChatMessage> consume(MessageRepository messageRepository,
        StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        return chatMessage -> {
            String subChannel = REDIS_CHANNEL_PREFIX + chatMessage.getChannelId();
            try {
                String messageJson = objectMapper.writeValueAsString(chatMessage);
                redisTemplate.convertAndSend(subChannel, messageJson);
            } catch (JsonProcessingException e) {
                log.info("JsonProcessingException = ", e);
            }
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
