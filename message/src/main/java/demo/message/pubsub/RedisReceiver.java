package demo.message.pubsub;

import static demo.message.constant.ChannelConstant.SUB_CHANNEL_URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.message.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisReceiver {

    private final SimpMessageSendingOperations messageSendingOperations;
    private final ObjectMapper objectMapper;

    public void receiveMessage(String messageJson) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(messageJson, ChatMessage.class);
            messageSendingOperations.convertAndSend(SUB_CHANNEL_URL + chatMessage.getChannelId(),
                chatMessage);
            log.info("Send message to client, message = {}", chatMessage.toString());
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException = {}", e.getMessage());
        }
    }
}
