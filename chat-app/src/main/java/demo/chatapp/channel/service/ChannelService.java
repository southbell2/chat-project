package demo.chatapp.channel.service;

import demo.chatapp.IdGenerator;
import demo.chatapp.channel.domain.Channel;
import demo.chatapp.channel.repository.ChannelRepository;
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
    private final IdGenerator idGenerator = new IdGenerator();

    @Autowired
    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public long createChannel(String title, Long masterId) {
        long id = idGenerator.nextId();
        User user = userRepository.findById(id);
        Channel channel = Channel.createChannel(id, title, user);
        channelRepository.saveChannel(channel);
        return id;
    }

}
