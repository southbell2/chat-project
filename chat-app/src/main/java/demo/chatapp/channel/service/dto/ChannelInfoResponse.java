package demo.chatapp.channel.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChannelInfoResponse {

    private String title;
    private Integer totalCount;
    private String masterNickname;
    private LocalDateTime createdAt;
}
