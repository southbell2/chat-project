package demo.chatapp.channel.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.chatapp.user.domain.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class EntryKey implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    EntryKey(Channel channel, User user) {
        Objects.requireNonNull(channel, "Channel은 null이 될 수 없습니다.");
        Objects.requireNonNull(user, "User는 null이 될 수 없습니다.");
        this.channel = channel;
        this.user = user;
    }
}
