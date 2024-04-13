package demo.chatapp.user.service.dto;

import demo.chatapp.user.domain.RoleType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoAdminResponse {

    private Long id;

    private String email;

    private String nickname;

    private LocalDateTime createdAt;

    private List<RoleType> roles;
}
