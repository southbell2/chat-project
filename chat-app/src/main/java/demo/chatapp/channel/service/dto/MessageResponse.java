package demo.chatapp.channel.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}
