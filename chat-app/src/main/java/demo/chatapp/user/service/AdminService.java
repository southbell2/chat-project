package demo.chatapp.user.service;

import static demo.chatapp.user.domain.RoleType.ROLE_ADMIN;
import static demo.chatapp.user.domain.RoleType.ROLE_USER;

import demo.chatapp.user.UserMapper;
import demo.chatapp.user.domain.RoleType;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.domain.UserRole;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.dto.PagedUserResponse;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UserInfoAdminResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public void signUpAdmin(SignUpUserRequest userRequest) {
        UserRole userRole = new UserRole(ROLE_USER);
        UserRole adminRole = new UserRole(ROLE_ADMIN);

        User user = User.createUser(userRequest, passwordEncoder, userRole, adminRole);
        userRepository.save(user);
    }

    public UserInfoAdminResponse getUserInfoByAdmin(Long userId) {
        User user = userRepository.findByIdWithRole(userId).orElseThrow();
        List<RoleType> roles = user.getUserRoles().stream()
            .map(UserRole::getRole)
            .toList();
        return userMapper.userToUserInfoAdminResponse(user, roles);
    }

    public List<PagedUserResponse> getPagedUsers(Long beforeId, Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Direction.DESC, "id"));
        List<User> pagedUsers = userRepository.findByIdLessThan(beforeId, pageRequest);
        return pagedUsers.stream()
            .map(pagedUser -> {
                List<RoleType> roles = pagedUser.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .toList();
                return userMapper.userToPagedUserResponse(pagedUser, roles);
            })
            .toList();
    }
}
