package demo.chatapp.user.service.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {

    private String email;
    private String nickname;
    private LocalDateTime createdAt;
}
