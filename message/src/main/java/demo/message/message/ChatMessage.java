package demo.message.message;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage {

    private Long channelId;
    private String nickname;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt;
    private Long messageId;
}
