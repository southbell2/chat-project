package chatapp.messageconsumer.message;

import chatapp.messageconsumer.message.casssandra.Message;
import chatapp.messageconsumer.message.casssandra.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {

}
