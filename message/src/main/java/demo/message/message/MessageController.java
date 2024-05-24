package demo.message.message;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        log.info("[SEND]start sending message");
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setMessageType(MessageType.CHAT);
        log.info("[SEND]ChatMessage = {}", chatMessage);
        messageService.sendMessage(chatMessage);
        log.info("[SEND]finish sending message");
    }

    @MessageMapping("/join")
    public void joinChannel(@Payload ChatMessage chatMessage) {
        log.info("[JOIN]start joining channel");
        Objects.requireNonNull(chatMessage, "chatMessage는 null이면 안 됩니다.");
        createJoinMessage(chatMessage);
        log.info("[JOIN]ChatMessage = {}", chatMessage);
        messageService.joinChannel(chatMessage);
        log.info("[JOIN]finish joining channel");
    }

    @MessageMapping("/leave")
    public void leaveChannel(@Payload ChatMessage chatMessage) {
        log.info("[LEAVE]start leaving channel");
        Objects.requireNonNull(chatMessage, "chatMessage는 null이면 안 됩니다.");
        createLeaveMessage(chatMessage);
        log.info("[LEAVE]ChatMessage = {}", chatMessage);
        messageService.leaveChannel(chatMessage);
        log.info("[LEAVE]finish leaving channel");
    }

    @GetMapping("/hello")
    public ResponseEntity<?> test() {
        log.info("test hello world");
        return ResponseEntity.ok("hello world!");
    }

    private void createJoinMessage(ChatMessage chatMessage) {
        chatMessage.setMessageType(MessageType.JOIN);
        chatMessage.setContent(chatMessage.getNickname() + "님이 입장하셨습니다.");
        chatMessage.setCreatedAt(LocalDateTime.now());
    }

    private void createLeaveMessage(ChatMessage chatMessage) {
        chatMessage.setMessageType(MessageType.LEAVE);
        chatMessage.setContent(chatMessage.getNickname() + "님이 퇴장하셨습니다.");
        chatMessage.setCreatedAt(LocalDateTime.now());
    }

}
