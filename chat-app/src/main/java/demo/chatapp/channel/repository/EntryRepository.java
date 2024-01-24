package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.domain.EntryKey;
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
}
