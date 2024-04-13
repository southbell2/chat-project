package demo.chatapp.security.principal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserPrincipal {

    private final Long id;
    private final String email;

}
