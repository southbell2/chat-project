package demo.chatapp.channel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import demo.chatapp.AbstractContainerEnv;
import demo.chatapp.channel.domain.Message;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class MessageRepositoryTest extends AbstractContainerEnv {

    @Autowired
    MessageRepository messageRepository;


    @Test
    void 메세지_읽어오기_테스트() {
        //given
        Long channelId = 1L;
        Integer bucket = 1;
        Message message1 = Message.createMessage(channelId, bucket, 1L, "kim", "first");
        Message message2 = Message.createMessage(channelId, bucket, 2L, "kim", "second");
        Message message3 = Message.createMessage(channelId, bucket, 3L, "kim", "third");
        Message message4 = Message.createMessage(channelId, bucket, 4L, "kim", "fourth");
        Message message5 = Message.createMessage(channelId, bucket, 5L, "kim", "fifth");
        messageRepository.save(message1);
        messageRepository.save(message2);
        messageRepository.save(message3);
        messageRepository.save(message4);
        messageRepository.save(message5);

        //when
        List<Message> messages = messageRepository.findMessages(channelId, bucket, 4L, 2);

        //then
        assertThat(messages.get(0).getMessageKey().getMessageId()).isEqualTo(3L);
        assertThat(messages.get(1).getMessageKey().getMessageId()).isEqualTo(2L);
    }
}