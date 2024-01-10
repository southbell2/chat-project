package demo.chatapp.user.service.dto;

import demo.chatapp.user.domain.RoleType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PagedUserResponse {
    private Long id;
    private String email;
    private List<RoleType> roles;
}
