package demo.chatapp.channel.service;

import demo.chatapp.IdGenerator;
import demo.chatapp.channel.domain.Bucket;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.repository.EntryRepository;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.channel.service.dto.MessageResponse;
import demo.chatapp.message.MessageMapper;
import demo.chatapp.message.domain.Message;
import demo.chatapp.message.repository.MessageRepository;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final EntryRepository entryRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final IdGenerator idGenerator = new IdGenerator();

    @Autowired
    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository,
        EntryRepository entryRepository, MessageRepository messageRepository,
        MessageMapper messageMapper) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }


    @Transactional
    public long createChannel(String title, Long masterId) {
        //채널 생성
        long id = idGenerator.nextId();
        User user = userRepository.findById(masterId).orElseThrow();
        Channel channel = Channel.createChannel(id, title, user);
        channelRepository.saveChannel(channel);

        //채널을 만든 유저가 채널 입장
        makeEntry(channel, user);

        return id;
    }

    @Transactional
    public JoinChannelResponse joinChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findByIdWithEntriesWithUser(channelId);
        User user = userRepository.findById(userId).orElseThrow();

        //entry(채널에 입장한 회원 기록) 저장
        makeEntry(channel, user);

        //채널 입장시 읽어 올 메세지 데이터
        List<MessageResponse> messageResponses = getMessageResponses(
            channelId, Long.MAX_VALUE, 20);

        return getJoinChannelResponse(
            channel, user, messageResponses);
    }

    public List<MessageResponse> getMessages(Long channelId, Long standardMessageId) {
        return getMessageResponses(channelId, standardMessageId, 20);
    }

    private void makeEntry(Channel channel, User user) {
        Entry entry = Entry.createEntry(channel, user);
        entryRepository.saveEntry(entry);
    }


    private List<MessageResponse> getMessageResponses(Long channelId, Long standardMessageId,
        Integer limit) {
        int bucket = getBucket(channelId);
        List<Message> messages = messageRepository.findMessages(channelId, bucket,
            standardMessageId,
            limit);
        return messages.stream()
            .map(messageMapper::messageToMessageResponse).toList();
    }

    private int getBucket(Long channelId) {
        long[] parsingId = idGenerator.parse(channelId);
        return Bucket.calculateBucket(parsingId[0]);
    }


    private JoinChannelResponse getJoinChannelResponse(Channel channel, User user,
        List<MessageResponse> messageResponses) {
        JoinChannelResponse joinChannelResponse = new JoinChannelResponse();
        joinChannelResponse.setTitle(channel.getTitle());
        joinChannelResponse.setCreatedAt(channel.getCreatedAt());

        channel.getEntries()    //기존 채팅방 유저의 닉네임 추가
            .forEach(e -> joinChannelResponse.getEntryNicknames().add(e.getUser().getNickname()));
        joinChannelResponse.getEntryNicknames().add(user.getNickname());    //현재 채팅방 입장한 유저의 닉네임 추가

        joinChannelResponse.setMessages(messageResponses);

        return joinChannelResponse;
    }

}
