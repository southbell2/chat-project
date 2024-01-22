package demo.chatapp.channel.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.driver.core.Session;
import demo.chatapp.AbstractContainerEnv;
import demo.chatapp.IdGenerator;
import demo.chatapp.channel.domain.Bucket;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.message.domain.Message;
import demo.chatapp.message.repository.MessageRepository;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = true)
class ChannelServiceTest extends AbstractContainerEnv{

    @Autowired
    ChannelService channelService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    EntityManager em;
    IdGenerator idGenerator = new IdGenerator();

    String testEmail;
    String testNickname;
    String testPassword;

    String joinEmail;
    String joinNickname;
    String joinPassword;

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

        joinEmail = "join@join.com";
        joinNickname = "joinUser";
        joinPassword = "join";
        userRequest.setEmail(joinEmail);
        userRequest.setNickname(joinNickname);
        userRequest.setPassword(joinPassword);
        userService.signUp(userRequest);
    }

    @AfterEach
    public void after() {
        Session session = cassandra.getCluster().connect();
        session.execute("TRUNCATE TABLE test.messages;");
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
        em.flush();
        em.clear();
        Channel channel = channelRepository.findByIdWithEntriesWithUser(channelId);


        //then
        assertThat(channelId).isGreaterThan(smallerId);
        assertThat(channel.getEntries().size()).isEqualTo(1);
    }

    @Test
    public void 채널_입장() {
        //given
        //먼저 채팅방 만들기
        String title = "test channel";
        User user = userRepository.findByEmailWithRole(testEmail);
        Long masterId = user.getId();
        long channelId = channelService.createChannel(title, masterId);

        //채팅방에 메세지 저장
        long[] parse = idGenerator.parse(channelId);
        Integer bucket = Bucket.calculateBucket(parse[0]);
        Message message1 = Message.createMessage(channelId, bucket, 1L, "kim", "first");
        Message message2 = Message.createMessage(channelId, bucket, 2L, "kim", "second");
        Message message3 = Message.createMessage(channelId, bucket, 3L, "kim", "third");
        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);

        //방에 입장할 유저 객체
        User joinUser = userRepository.findByEmailWithRole(joinEmail);
        em.flush();
        em.clear();

        //when
        JoinChannelResponse joinChannelResponse = channelService.joinChannel(channelId,
            joinUser.getId());

        //then
        assertThat(joinChannelResponse.getTitle()).isEqualTo(title);
        assertThat(joinChannelResponse.getEntryNicknames().size()).isEqualTo(2);
        assertThat(joinChannelResponse.getMessages().size()).isEqualTo(3);
    }

    @Test
    void 메세지_읽어오기_테스트() {
        //given
        Long channelId = 1L;
        Integer bucket = 1;
        Message message1 = Message.createMessage(channelId, bucket, 1L, "kim", "first");
        Message message2 = Message.createMessage(channelId, bucket, 2L, "kim", "second");
        Message message3 = Message.createMessage(channelId, bucket, 3L, "kim", "third");
        Message message4 = Message.createMessage(channelId, bucket, 4L, "kim", "fourth");
        Message message5 = Message.createMessage(channelId, bucket, 5L, "kim", "fifth");
        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);
        messageRepository.save(message5);

        //when
        List<Message> messages = messageRepository.findMessages(channelId, bucket, 4L, 2);

        //then
        assertThat(messages.get(0).getMessageKey().getMessageId()).isEqualTo(3L);
        assertThat(messages.get(1).getMessageKey().getMessageId()).isEqualTo(2L);
    }
}

