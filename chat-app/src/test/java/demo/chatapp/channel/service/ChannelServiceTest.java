package demo.chatapp.channel.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.driver.core.Session;
import demo.chatapp.AbstractContainerEnv;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.repository.EntryRepository;
import demo.chatapp.channel.service.dto.ChannelInfoResponse;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.id.ThreadNameQueue;
import demo.chatapp.id.Bucket;
import demo.chatapp.id.IdGeneratorMap;
import demo.chatapp.message.domain.Message;
import demo.chatapp.message.repository.MessageRepository;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@SpringBootTest
@ActiveProfiles({"test", "id-nosync"})
class ChannelServiceTest extends AbstractContainerEnv {

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

    String testEmail;
    String testNickname;
    String testPassword;

    String joinEmail;
    String joinNickname;
    String joinPassword;
    static int maxThreadName;
    @Autowired
    private EntryRepository entryRepository;

    @BeforeAll
    public static void threadSetUp() {
        maxThreadName = 10;
        for (int i = 0; i < maxThreadName; i++) {
            ThreadNameQueue.add(i);
        }
        IdGeneratorMap.initMap(maxThreadName);
        Thread.currentThread().setName("0");
    }

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
        User user = userRepository.findByEmailWithRole(testEmail).get();
        Long masterId = user.getId();
        long smallerId = IdGeneratorMap.idGeneratorMap.get(Integer.parseInt(Thread.currentThread().getName())).nextId();
        Thread.sleep(100L);

        //when
        long channelId = channelService.createChannel(title, masterId);
        em.flush();
        em.clear();
        Channel channel = channelRepository.findByIdWithEntriesWithUser(channelId).get();

        //then
        assertThat(channelId).isGreaterThan(smallerId);
        assertThat(channel.getEntries().size()).isEqualTo(1);
    }

    @Test
    public void 채널_입장() {
        //given
        //먼저 채팅방 만들기
        String title = "test channel";
        User user = userRepository.findByEmailWithRole(testEmail).get();
        Long masterId = user.getId();
        long channelId = channelService.createChannel(title, masterId);

        //채팅방에 메세지 저장
        long[] parse = IdGeneratorMap.idGeneratorMap.get(Integer.parseInt(Thread.currentThread().getName())).parse(channelId);
        Integer bucket = Bucket.calculateBucket(parse[0]);
        Message message1 = Message.createMessage(channelId, bucket, 1L, "kim", "first");
        Message message2 = Message.createMessage(channelId, bucket, 2L, "kim", "second");
        Message message3 = Message.createMessage(channelId, bucket, 3L, "kim", "third");
        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);

        //방에 입장할 유저 객체
        User joinUser = userRepository.findByEmailWithRole(joinEmail).get();
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

    @Test
    void 채널나가기_한명있는경우() {
        //given
        //채팅방 만들기
        String title = "test channel";
        User user = userRepository.findByEmailWithRole(testEmail).get();
        Long masterId = user.getId();
        long channelId = channelService.createChannel(title, masterId);
        em.flush();
        em.clear();

        //when
        channelService.leaveChannel(channelId, masterId);
        em.flush();
        em.clear();

        //then
        Optional<Entry> entryOptional = entryRepository.findByChannelIdAndUserId(channelId,
            masterId);
        assertThat(entryOptional.isEmpty()).isTrue();

        Optional<Channel> channelOptional = channelRepository.findById(masterId);
        assertThat(channelOptional.isEmpty()).isTrue();
    }

    @Test
    void 채널나나기_두명있는경우() {
        //given
        //채팅방 만들기
        String title = "test channel";
        User user = userRepository.findByEmailWithRole(testEmail).get();
        Long masterId = user.getId();
        long channelId = channelService.createChannel(title, masterId);
        em.flush();
        em.clear();

        //채팅방 입장
        User joinUser = userRepository.findByEmailWithRole(joinEmail).get();
        channelService.joinChannel(channelId, joinUser.getId());
        em.flush();
        em.clear();

        //when
        channelService.leaveChannel(channelId, masterId);
        em.flush();
        em.clear();

        //then
        Optional<Entry> masterOptional = entryRepository.findByChannelIdAndUserId(channelId,
            masterId);
        Optional<Entry> joinOptional = entryRepository.findByChannelIdAndUserId(channelId,
            joinUser.getId());
        assertThat(masterOptional.isEmpty()).isTrue();
        assertThat(joinOptional.isPresent()).isTrue();

        Channel channel = channelRepository.findById(channelId).get();
        assertThat(channel.getTotalCount()).isEqualTo(1);

    }

    @Test
    void 채널_목록_보기() {
        //given
        User user = userRepository.findByEmailWithRole(testEmail).get();
        //채널 5개 만들기
        channelService.createChannel("channel1", user.getId());
        channelService.createChannel("channel2", user.getId());
        channelService.createChannel("channel3", user.getId());
        long channelId = channelService.createChannel("channel4", user.getId());
        channelService.createChannel("channel5", user.getId());

        //when
        List<ChannelInfoResponse> channelInfo = channelService.getChannelInfo(channelId, 2);

        //then
        assertThat(channelInfo.size()).isEqualTo(2);
        assertThat(channelInfo.get(0).getTitle()).isEqualTo("channel3");
        assertThat(channelInfo.get(1).getTitle()).isEqualTo("channel2");

    }

    @Test
    void 내가_입장한_채널목록보기() {
        //given
        User createUser = userRepository.findByEmailWithRole(testEmail).get();
        long channel2 = channelService.createChannel("channel2", createUser.getId());
        long channel1 = channelService.createChannel("channel1", createUser.getId());
        long channel5 = channelService.createChannel("channel5", createUser.getId());
        long channel3 = channelService.createChannel("channel3", createUser.getId());
        long channel4 = channelService.createChannel("channel4", createUser.getId());

        //1,2,3,4,5 순서대로 채널에 입장
        User joinUser = userRepository.findByEmailWithRole(joinEmail).get();
        channelService.joinChannel(channel1, joinUser.getId());
        channelService.joinChannel(channel2, joinUser.getId());
        channelService.joinChannel(channel3, joinUser.getId());
        channelService.joinChannel(channel4, joinUser.getId());
        channelService.joinChannel(channel5, joinUser.getId());

        //when
        List<ChannelInfoResponse> myChannelInfo = channelService.getMyChannelInfo(joinUser.getId(),
            1, 2);

        //then
        assertThat(myChannelInfo.size()).isEqualTo(2);
        assertThat(myChannelInfo.get(0).getTitle()).isEqualTo("channel3");
        assertThat(myChannelInfo.get(1).getTitle()).isEqualTo("channel2");
    }
}

