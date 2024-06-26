package demo.chatapp.message.domain;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
@EqualsAndHashCode(exclude = "bucket")
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class MessageKey implements Serializable {

    @PrimaryKeyColumn(
        name = "channel_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED
    )
    private Long channelId;

    @PrimaryKeyColumn(
        name = "bucket", ordinal = 1, type = PrimaryKeyType.PARTITIONED
    )
    private Integer bucket;

    @PrimaryKeyColumn(
        name = "message_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING
    )
    private Long messageId;

}
