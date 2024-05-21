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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Async("consumerThreadPoolTaskExecutor")
    public void saveMessageInCassandra(ChatMessage chatMessage) {
        Message message = createMessage(chatMessage);
        try {
            messageRepository.save(message);
            log.info("saveMessageInCassandra message = {}", message.toString());
        } catch (DataAccessException e) {
            log.error("Error Message = {} , Message = {}", e.getMessage(), message.toString());
        }
    }

    @Async("consumerThreadPoolTaskExecutor")
    public void publishRedis(ChatMessage chatMessage) {
        String subChannel = REDIS_CHANNEL_PREFIX + chatMessage.getChannelId();
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
            log.info("publishRedis messageJson = {}", messageJson);
        } catch (JsonProcessingException e) {
            log.error("Error Message = {}", e.getMessage());
        } catch (RedisException e) {
            log.error("Error Message = {} , Message = {}", e.getMessage(), chatMessage.toString());
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