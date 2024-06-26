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
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setMessageType(MessageType.CHAT);
        messageService.sendMessage(chatMessage);
    }

    @MessageMapping("/join")
    public void joinChannel(@Payload ChatMessage chatMessage) {
        Objects.requireNonNull(chatMessage, "chatMessage는 null이면 안 됩니다.");
        createJoinMessage(chatMessage);
        messageService.joinChannel(chatMessage);
    }

    @MessageMapping("/leave")
    public void leaveChannel(@Payload ChatMessage chatMessage) {
        Objects.requireNonNull(chatMessage, "chatMessage는 null이면 안 됩니다.");
        createLeaveMessage(chatMessage);
        messageService.leaveChannel(chatMessage);
    }

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running");
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
