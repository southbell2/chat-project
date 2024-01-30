package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.domain.EntryKey;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntryRepository extends JpaRepository<Entry, EntryKey> {

    @Modifying
    @Query("DELETE FROM Entry e"
        + " WHERE e.entryKey.channel.id = :channelId AND e.entryKey.user.id = :userId")
    void deleteByChannelIdAndUserId(@Param("channelId") Long channelId,
        @Param("userId") Long userId);

    @Query("SELECT e FROM Entry e WHERE e.entryKey.channel.id = :channelId AND e.entryKey.user.id = :userId")
    Optional<Entry> findByChannelIdAndUserId(@Param("channelId") Long channelId,
        @Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO entries (channel_id, user_id) VALUES (:channelId, :userId)", nativeQuery = true)
    void saveEntry(@Param("channelId") Long channelId, @Param("userId") Long userId);

    @Query("SELECT e FROM Entry e"
        + " JOIN FETCH e.entryKey.channel c"
        + " JOIN FETCH e.entryKey.channel.user u"
        + " WHERE e.entryKey.user.id = :userId")
    Slice<Entry> findEntriesByUserIdWithChannelWithUser(@Param("userId") Long userId, Pageable pageable);
}
