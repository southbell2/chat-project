package demo.chatapp.channel.service.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinChannelResponse {

    private String title;
    private LocalDateTime createdAt;
    private List<String> entryNicknames = new ArrayList<>();
    private List<MessageResponse> messages = new ArrayList<>();
}
