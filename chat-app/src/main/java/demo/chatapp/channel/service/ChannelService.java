package demo.chatapp.channel.service;

import demo.chatapp.IdGenerator;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.repository.ChannelRepository;
import demo.chatapp.channel.repository.EntryRepository;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.user.domain.User;
import demo.chatapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final EntryRepository entryRepository;
    private final IdGenerator idGenerator = new IdGenerator();

    @Autowired
    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository,
        EntryRepository entryRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
    }

    @Autowired

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

        Entry entry = Entry.createEntry(channel, user);
        entryRepository.saveEntry(entry);

        JoinChannelResponse joinChannelResponse = new JoinChannelResponse();
        joinChannelResponse.setTitle(channel.getTitle());
        joinChannelResponse.setCreatedAt(channel.getCreatedAt());
        channel.getEntries()
            .forEach(e -> joinChannelResponse.getEntryNicknames().add(e.getUser().getNickname()));

        return joinChannelResponse;
    }

}
