package demo.chatapp.message.domain;


import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "messageKey")
public class Message {

    @PrimaryKey
    MessageKey messageKey;

    @Column
    private String nickname;

    @Column
    private String content;

    @Column(value = "created_at")
    LocalDateTime createdAt;

    public static Message createMessage(Long channelId, Integer bucket, Long messageId, String nickname, String content) {
        MessageKey messageKey = new MessageKey(channelId, bucket, messageId);
        Message message = new Message();
        message.setMessageKey(messageKey);
        message.setNickname(nickname);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    private void setMessageKey(MessageKey messageKey) {
        Objects.requireNonNull(messageKey, "MessegeKey 객체는 null이 될 수 없습니다.");
        this.messageKey = messageKey;
    }

    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void setContent(String content) {
        this.content = content;
    }

    private void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
