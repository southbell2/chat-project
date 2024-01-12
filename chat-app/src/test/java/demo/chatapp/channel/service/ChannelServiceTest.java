package demo.chatapp.channel.service;

import demo.chatapp.IdGenerator;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = true)   //embedded redis port 충돌 때문에 추가
class ChannelServiceTest {

    @Autowired
    ChannelService channelService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    IdGenerator idGenerator = new IdGenerator();

    String testEmail;
    String testNickname;
    String testPassword;

    @BeforeEach
    public void setUp() {
        SignUpUserRequest userRequest = new SignUpUserRequest();
        testEmail = "test@test.com";
        testNickname = "Terry";
        testPassword = "12345";
        userRequest.setEmail(testEmail);
        userRequest.setNickname(testNickname);
        userRequest.setPassword(testPassword);

        userService.signUp(userRequest);
    }

    @Test
    public void 채널_만들기() throws InterruptedException {
        //given
        String title = "test channel";
        User user = userRepository.findByEmailWithRole(testEmail);
        Long masterId = user.getId();
        long smallerId = idGenerator.nextId();
        Thread.sleep(100L);

        //when
        long channelId = channelService.createChannel(title, masterId);

        //then
        assertThat(channelId).isGreaterThan(smallerId);
    }

}

