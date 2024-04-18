package chatapp.messageconsumer.function;

import static chatapp.messageconsumer.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import chatapp.messageconsumer.id.Bucket;
import chatapp.messageconsumer.id.IdGenerator;
import chatapp.messageconsumer.id.IdGeneratorMap;
import chatapp.messageconsumer.message.ChatMessage;
import chatapp.messageconsumer.message.MessageRepository;
import chatapp.messageconsumer.message.casssandra.Message;
import chatapp.messageconsumer.service.ConsumerTaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Slf4j
public class MessageConsumer {

    @Bean
    public Consumer<ChatMessage> consume(ConsumerTaskService service) {
        return chatMessage -> {
            service.publishRedis(chatMessage);
            service.saveMessageInCassandra(createMessage(chatMessage));
        };
    }

    private Message createMessage(ChatMessage chatMessage) {
        Integer threadName = Integer.parseInt(Thread.currentThread().getName());
        IdGenerator idGenerator = IdGeneratorMap.idGeneratorMap.get(threadName);
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
