package demo.chatapp.channel.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.chatapp.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "entries")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Entry {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime joinedAt;

    public static Entry createEntry(Channel channel, User user) {
        Entry entry = new Entry();
        entry.setChannel(channel);
        entry.setUser(user);
        return entry;
    }

    private void setChannel(Channel channel) {
        Objects.requireNonNull(channel, "Channel 객체는 null이 될 수 없습니다.");
        this.channel = channel;
    }

    private void setUser(User user) {
        Objects.requireNonNull(user, "User 객체는 null이 될 수 없습니다.");
        this.user = user;
    }
}
