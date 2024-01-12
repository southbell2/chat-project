package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Channel;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChannelRepository {

    private final EntityManager em;

    public void saveChannel(Channel channel) {
        em.persist(channel);
    }

}
