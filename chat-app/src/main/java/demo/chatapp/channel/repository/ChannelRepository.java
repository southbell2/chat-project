package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Channel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    @Query("SELECT DISTINCT c FROM Channel c"
        + " JOIN FETCH c.entries e"
        + " JOIN FETCH e.entryKey.user u"
        + " WHERE c.id = :id")
    Optional<Channel> findByIdWithEntriesWithUser(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Channel c SET c.totalCount = c.totalCount + :value WHERE c.id = :id")
    void updateTotalCount(@Param("value") Integer value, @Param("id") Long id);

    @Query("SELECT c FROM Channel c"
        + " JOIN FETCH c.user"
        + " WHERE c.id < :standardId")
    List<Channel> findChannelsByIdWithUser(@Param("standardId") Long standardId, Pageable pageable);
}
