package demo.chatapp.channel.domain;


import java.time.LocalDateTime;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Message {

    @PrimaryKey
    MessageKey messageKey;

    @Column
    private String nickname;

    @Column
    private String content;

    @Column
    LocalDateTime createdAt;

}
