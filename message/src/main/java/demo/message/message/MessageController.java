package demo.message.message;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessageSendingOperations template;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setCreatedAt(LocalDateTime.now());
    }

}
