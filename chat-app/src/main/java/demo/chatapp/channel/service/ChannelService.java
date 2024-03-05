package demo.chatapp.channel.service;

import demo.chatapp.id.IdGenerator;
import demo.chatapp.id.Bucket;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.repository.EntryRepository;
import demo.chatapp.channel.service.dto.ChannelInfoResponse;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.channel.service.dto.MessageResponse;
import demo.chatapp.message.MessageMapper;
import demo.chatapp.message.domain.Message;
import demo.chatapp.message.repository.MessageRepository;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
        channelRepository.save(channel);

        //채널을 만든 유저가 채널 입장
        makeEntry(channel, user);

        return id;
    }

    @Transactional
    public JoinChannelResponse joinChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findByIdWithEntriesWithUser(channelId).orElseThrow();
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

    @Transactional
    public void leaveChannel(Long channelId, Long userId) {
        entryRepository.deleteByChannelIdAndUserId(channelId, userId);
        Channel channel = channelRepository.findById(channelId).orElseThrow();
        //현재 남은 방의 인원이 혼자 밖에 없으면 채널을 삭제하고 그게 아닐시 인원수를 1 줄인다.
        if (channel.getTotalCount() <= 1) {
            channelRepository.delete(channel);
        } else {
            channelRepository.updateTotalCount(-1, channelId);
        }
    }

    public List<ChannelInfoResponse> getChannelInfo(long standardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Direction.DESC, "id"));
        List<Channel> channels = channelRepository.findChannelsByIdWithUser(standardId,
            pageRequest);

        return channels.stream()
            .map(this::channelToChannelInfoResponse)
            .toList();
    }

    public List<ChannelInfoResponse> getMyChannelInfo(Long userId, int page, int limit) {
        //채널에 최근에 입장한 순서대로 정렬
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Direction.DESC, "joinedAt"));
        List<Entry> entries = entryRepository.findEntriesByUserIdWithChannelWithUser(
            userId, pageRequest).getContent();

        return entries.stream()
            .map(this::entryToChannelInfoResponse)
            .toList();
    }

    private void makeEntry(Channel channel, User user) {
        Entry entry = Entry.createEntry(channel, user);
        entryRepository.saveEntry(channel.getId(), user.getId());
        channel.getEntries().add(entry);
        channelRepository.updateTotalCount(1, channel.getId());
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

        channel.getEntries()
            .forEach(e -> joinChannelResponse.getEntryNicknames()
                .add(e.getEntryKey().getUser().getNickname()));

        joinChannelResponse.setMessages(messageResponses);

        return joinChannelResponse;
    }

    private ChannelInfoResponse channelToChannelInfoResponse(Channel channel) {
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();
        channelInfoResponse.setCreatedAt(channel.getCreatedAt());
        channelInfoResponse.setMasterNickname(channel.getUser().getNickname());
        channelInfoResponse.setTotalCount(channel.getTotalCount());
        channelInfoResponse.setTitle(channel.getTitle());
        return channelInfoResponse;
    }

    private ChannelInfoResponse entryToChannelInfoResponse(Entry entry) {
        Channel channel = entry.getEntryKey().getChannel();
        return channelToChannelInfoResponse(channel);
    }

}
