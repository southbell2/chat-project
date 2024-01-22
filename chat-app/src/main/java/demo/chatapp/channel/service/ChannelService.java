package demo.chatapp.channel.service;

import demo.chatapp.IdGenerator;
import demo.chatapp.channel.MessageMapper;
import demo.chatapp.channel.domain.Bucket;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.domain.Message;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.repository.EntryRepository;
import demo.chatapp.channel.repository.MessageRepository;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.channel.service.dto.MessageResponse;
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
        long id = idGenerator.nextId();
        User user = userRepository.findById(id);
        Channel channel = Channel.createChannel(id, title, user);
        channelRepository.saveChannel(channel);
        return id;
    }

    @Transactional
    public JoinChannelResponse joinChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findByIdWithEntriesWithUser(channelId);
        User user = userRepository.findById(userId);

        //entry(채널에 입장한 회원 기록) 저장
        Entry entry = Entry.createEntry(channel, user);
        entryRepository.saveEntry(entry);

        int bucket = getBucket(channelId);
        List<Message> messages = messageRepository.findMessages(channelId, bucket, Long.MAX_VALUE,
            20);
        List<MessageResponse> messageResponses = messages.stream()
            .map(messageMapper::messageToMessageResponse).toList();

        //채널에 입장한 회원에게 필요한 정보 얻기
        JoinChannelResponse joinChannelResponse = new JoinChannelResponse();
        joinChannelResponse.setTitle(channel.getTitle());
        joinChannelResponse.setCreatedAt(channel.getCreatedAt());
        channel.getEntries()
            .forEach(e -> joinChannelResponse.getEntryNicknames().add(e.getUser().getNickname()));
        joinChannelResponse.setMessages(messageResponses);

        return joinChannelResponse;
    }

    private int getBucket(Long channelId) {
        long[] parsingId = idGenerator.parse(channelId);
        return Bucket.calculateBucket(parsingId[0]);
    }

}
