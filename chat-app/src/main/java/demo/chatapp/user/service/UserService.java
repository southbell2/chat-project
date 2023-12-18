package demo.chatapp.user.service;

import demo.chatapp.user.UserMapper;
import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.domain.UserRole;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UserInfoResponse;
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
    private final UserMapper userMapper;

    @Transactional
    public void signUp(SignUpUserRequest userRequest) {
        UserRole userRole = new UserRole(RoleType.ROLE_USER);
        User user = User.createUser(userRequest, passwordEncoder, userRole);
        userRepository.saveUser(user);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId);
        return userMapper.userToUserInfoResponse(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        userRepository.deleteUser(user);
    }
}
