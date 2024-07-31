package chatapp.messageconsumer.service;

import static chatapp.messageconsumer.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import chatapp.messageconsumer.id.Bucket;
import chatapp.messageconsumer.id.generator.IdGenerator;
import chatapp.messageconsumer.id.manager.IdGeneratorManager;
import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.message.MessageRepository;
import chatapp.messageconsumer.message.casssandra.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerTaskService {

    private final MessageRepository messageRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final IdGeneratorManager idGeneratorManager;
    private final RetryTemplate retryTemplate;

    @Async("consumerThreadPoolTaskExecutor")
    public void saveMessageInCassandra(List<ChatMessage> chatMessageList) {
        for(ChatMessage chatMessage : chatMessageList) {
            Message message = createMessage(chatMessage);
            try {
                retryTemplate.execute(context -> {
                    messageRepository.save(message);
                    context.setAttribute("message", message);
                    return null;
                });
            } catch (Exception e) {
                log.error("[ERROR] 카산드라 메시지 저장 최종 실패, msg = {}, ex = {}", message, e);
            }
        }
    }

    @Async("consumerThreadPoolTaskExecutor")
    public void publishRedis(List<ChatMessage> chatMessageList) {
        for(ChatMessage chatMessage : chatMessageList) {
            String subChannel = REDIS_CHANNEL_PREFIX + chatMessage.getChannelId();
            try {
                String messageJson = objectMapper.writeValueAsString(chatMessage);
                redisTemplate.convertAndSend(subChannel, messageJson);
            } catch (JsonProcessingException e) {
                log.error("[ERROR} JsonProcessing 예외, msg = {}, ex = {}",chatMessage, e);
            } catch (RedisException e) {
                log.error("[ERROR] Redis Publish 중 예외 발생, msg = {} , ex = {}", chatMessage, e);
            }
        }
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
