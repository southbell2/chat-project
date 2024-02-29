package demo.message.message;

import static demo.message.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final StringRedisTemplate redisTemplate;
    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter listenerAdapter;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, Integer> hashMap = new ConcurrentHashMap<>();
    private final StreamBridge streamBridge;

    public void joinChannel(ChatMessage chatMessage) {
        //채널을 입장하면 redis에 채널을 구독하고 채널의 다른 회원들에게 입장 메세지를 보낸다.
        Long channelId = chatMessage.getChannelId();
        String subChannel = REDIS_CHANNEL_PREFIX + channelId;
        Integer count = hashMap.put(channelId, hashMap.getOrDefault(channelId, 0) + 1);
        if (count == null) {
            container.addMessageListener(listenerAdapter, new ChannelTopic(subChannel));
        }
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException = ", e);
        }
    }

    public void leaveChannel(ChatMessage chatMessage) {
        //채널을 퇴장하면 redis에 채널 구독을 끊고 채널의 다른 회원들에게 퇴장 메세지를 보낸다.
        Long channelId = chatMessage.getChannelId();
        String subChannel = REDIS_CHANNEL_PREFIX + channelId;
        Integer count = hashMap.put(channelId, hashMap.get(channelId) - 1);
        if (count == 1) {
            container.removeMessageListener(listenerAdapter, new ChannelTopic(subChannel));
            hashMap.remove(channelId);
        }
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException = ", e);
        }
    }

    @Async
    public void sendMessage(ChatMessage chatMessage) {
        Message<ChatMessage> message = MessageBuilder.withPayload(chatMessage)
            .setHeader("partitionKey", chatMessage.getChannelId())
            .build();
        streamBridge.send("output", message);
    }

}
