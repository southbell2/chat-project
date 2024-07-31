package demo.message.message;

import static demo.message.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.message.pubsub.RedisSubManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final StreamBridge streamBridge;
    private final RedisSubManager redisSubManager;

    public void joinChannel(ChatMessage chatMessage) {
        //채널을 입장하면 redis에 채널을 구독하고 채널의 다른 회원들에게 입장 메세지를 보낸다.
        Long channelId = chatMessage.getChannelId();
        redisSubManager.subIfNecessary(channelId);

        String subChannel = REDIS_CHANNEL_PREFIX + channelId;
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
        } catch (JsonProcessingException e) {
            log.error("[ERROR} JsonProcessing 예외, msg = {}, ex = {}",chatMessage, e);
        } catch (Exception e) {
            log.error("[ERROR]레디스로 JOIN 메세지 publish 중 에러 발생, msg = {}, ex = {}", chatMessage, e);
        }
    }

    public void leaveChannel(ChatMessage chatMessage) {
        //채널을 퇴장하면 redis에 채널 구독을 끊고 채널의 다른 회원들에게 퇴장 메세지를 보낸다.
        Long channelId = chatMessage.getChannelId();
        redisSubManager.unSubIfNecessary(channelId);

        String subChannel = REDIS_CHANNEL_PREFIX + channelId;
        try {
            String messageJson = objectMapper.writeValueAsString(chatMessage);
            redisTemplate.convertAndSend(subChannel, messageJson);
        } catch (JsonProcessingException e) {
            log.error("[ERROR} JsonProcessing 예외, msg = {}, ex = {}",chatMessage, e);
        } catch (Exception e) {
            log.error("[ERROR]레디스로 LEAVE 메세지 publish 중 예외 발생, msg = {}, ex = {}", chatMessage, e);
        }
    }

    public void sendMessage(ChatMessage chatMessage) {
        Message<ChatMessage> message = MessageBuilder.withPayload(chatMessage)
            .setHeader("partitionKey", chatMessage.getChannelId())
            .build();
        try {
            streamBridge.send("output", message);
        } catch (Exception e) {
            log.error("[ERROR]카프카로 메세지 전송 중 예외 발생, msg = {}, ex = {}", chatMessage, e);
        }
    }

}
