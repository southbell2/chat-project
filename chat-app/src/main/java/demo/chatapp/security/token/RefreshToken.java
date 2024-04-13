package demo.chatapp.security.token;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshToken {
    private String token;
    private Long userId;
    private String email;
    private Long exp;
    private Set<String> authorities;
}
