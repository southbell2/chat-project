package demo.chatapp.message.repository;

import demo.chatapp.message.domain.Message;
import demo.chatapp.message.domain.MessageKey;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {

    @Query("SELECT * FROM messages WHERE channel_id = :channelId AND bucket = :bucket AND message_id < :messageId LIMIT :limit")
    List<Message> findMessages(@Param("channelId") Long channelId, @Param("bucket") Integer bucket,
        @Param("messageId") Long messageId, @Param("limit") Integer limit);
}
