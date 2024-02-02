package demo.message.message;

import static demo.message.constant.ChannelConstant.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.message.constant.ChannelConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter listenerAdapter;
    private final ObjectMapper objectMapper;

    public void joinChannel(ChatMessage chatMessage) {
        //채널을 입장하면 redis에 채널을 구독하고 채널의 다른 회원들에게 입장 메세지를 보낸다.
        String subChannel = REDIS_CHANNEL_PREFIX + chatMessage.getChannelId();
        container.addMessageListener(listenerAdapter, new PatternTopic(subChannel));
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException = ", e);
        }
    }

}
