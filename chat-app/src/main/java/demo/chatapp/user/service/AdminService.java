package demo.chatapp.user.service;

import static demo.chatapp.user.domain.RoleType.ROLE_ADMIN;
import static demo.chatapp.user.domain.RoleType.ROLE_USER;

import demo.chatapp.user.UserMapper;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.domain.UserRole;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUpAdmin(SignUpUserRequest userRequest) {
        UserRole userRole = new UserRole(ROLE_USER);
        UserRole adminRole = new UserRole(ROLE_ADMIN);

        User user = User.createUser(userRequest, passwordEncoder, userRole, adminRole);
        userRepository.saveUser(user);
    }
}
