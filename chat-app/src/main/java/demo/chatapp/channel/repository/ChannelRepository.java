package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Channel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Query("SELECT DISTINCT c FROM Channel c"
        + " JOIN FETCH c.entries e"
        + " JOIN FETCH e.user u"
        + " WHERE c.id = :id")
    Optional<Channel> findByIdWithEntriesWithUser(@Param("id") Long id);
}
