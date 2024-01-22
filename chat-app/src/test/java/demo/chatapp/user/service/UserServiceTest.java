package demo.chatapp.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import demo.chatapp.AbstractContainerEnv;
import demo.chatapp.exception.UnauthorizedException;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UpdatePasswordRequest;
import demo.chatapp.user.service.dto.UpdateUserInfoRequest;
import demo.chatapp.user.service.dto.UserInfoResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = true)
class UserServiceTest extends AbstractContainerEnv {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @Test
    public void 회원_가입_성공() {
        //Given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword("12345");

        //When
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        //Then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    public void 회원_가입_실패() {
        //Given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword("12345");
        userService.signUp(userRequest);

        //When && Then
        assertThatThrownBy(() -> userService.signUp(userRequest))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void 회원_정보_얻기() {
        //given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword("12345");
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        //when
        UserInfoResponse userInfo = userService.getUserInfo(user.getId());

        //then
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getNickname()).isEqualTo(nickname);
    }

    @Test
    public void 회원_정보_얻기_실패() {
        //given
        Long id = 1L;

        //when && then
        assertThat(userService.getUserInfo(id)).isNull();

    }

    @Test
    public void 회원_정보_수정() {
        //given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword("12345");
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        nickname = "Spring";
        UpdateUserInfoRequest userInfoRequest = new UpdateUserInfoRequest();
        userInfoRequest.setNickname(nickname);

        //when
        userService.updateUserInfo(user.getId(), userInfoRequest);

        //then
        assertThat(user.getNickname()).isEqualTo(nickname);

    }

    @Test
    public void 비밀번호_수정() {
        //given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        String nowPassword = "12345";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword(nowPassword);
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        String newPassword = "qwerasdf";
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest();
        passwordRequest.setNowPassword(nowPassword);
        passwordRequest.setNewPassword(newPassword);

        //when
        userService.updatePassword(user.getId(), passwordRequest);

        //then
        assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();
    }

    @Test
    public void 비밀번호_수정_실패() {
        //given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        String nowPassword = "12345";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword(nowPassword);
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        //현재 비밀번호를 틀린 비밀번호로 입력
        String newPassword = "qwerasdf";
        UpdatePasswordRequest passwordRequest = new UpdatePasswordRequest();
        passwordRequest.setNowPassword(newPassword);
        passwordRequest.setNewPassword(newPassword);

        //when && then
        assertThatThrownBy(() -> userService.updatePassword(user.getId(), passwordRequest))
            .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    public void 회원_탈퇴() {
        //given
        SignUpUserRequest userRequest = new SignUpUserRequest();
        String email = "test@test.com";
        String nickname = "Terry";
        userRequest.setEmail(email);
        userRequest.setNickname(nickname);
        userRequest.setPassword("12345");
        userService.signUp(userRequest);
        User user = userRepository.findByEmailWithRole(email);

        //when
        userService.deleteUser(user.getId());
        em.flush();
        em.clear();

        //then
        assertThat(userService.getUserInfo(user.getId())).isNull();
    }
}