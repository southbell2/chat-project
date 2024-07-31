package demo.chatapp.user.service;

import demo.chatapp.exception.BadRequestException;
import demo.chatapp.user.UserMapper;
import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.domain.UserRole;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UpdatePasswordRequest;
import demo.chatapp.user.service.dto.UpdateUserInfoRequest;
import demo.chatapp.user.service.dto.UserInfoResponse;
import java.util.Optional;
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
        userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return userMapper.userToUserInfoResponse(user.orElseThrow(() -> new BadRequestException("회원 ID를 찾을 수 없습니다.")));
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUserInfo(Long userId, UpdateUserInfoRequest userInfoRequest) {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new BadRequestException("회원 ID를 찾을 수 없습니다.")).updateUserInfo(userInfoRequest);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest updatePasswordRequest) {
        Optional<User> user = userRepository.findById(userId);
        user.orElseThrow(() -> new BadRequestException("회원 ID를 찾을 수 없습니다.")).updatePassword(updatePasswordRequest.getNewPassword(),
            updatePasswordRequest.getNowPassword(), passwordEncoder);
    }
}
