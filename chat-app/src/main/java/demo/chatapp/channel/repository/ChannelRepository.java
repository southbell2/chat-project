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

    public Channel findByIdWithEntriesWithUser(Long id) {
        return em.createQuery(
                "SELECT DISTINCT c FROM Channel c"
                    + " JOIN FETCH c.entries e"
                    + " JOIN FETCH e.user"
                    + " WHERE c.id = :id", Channel.class)
            .setParameter("id", id)
            .getSingleResult();
    }
}
