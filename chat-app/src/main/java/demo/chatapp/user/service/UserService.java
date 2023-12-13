package demo.chatapp.user.service;

import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.domain.UserRole;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.vo.SignUpUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpUserRequest userRequest) {
        UserRole userRole = new UserRole(RoleType.ROLE_USER);
        User user = User.createUser(userRequest, passwordEncoder, userRole);
        userRepository.saveUser(user);
    }
}
