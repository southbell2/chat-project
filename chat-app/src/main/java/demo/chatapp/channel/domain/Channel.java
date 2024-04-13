package demo.chatapp.channel.domain;

import demo.chatapp.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "channels")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user", "entries"})
public class Channel implements Persistable<Long> {

    @Id
    @Column(name = "channel_id")
    private Long id;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "VARCHAR(30) NOT NULL")
    private String title;

    @Column(name = "total_count")
    private Integer totalCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User user;

    @OneToMany(mappedBy = "entryKey.channel")
    private List<Entry> entries = new ArrayList<>();

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public static Channel createChannel(Long id, String title, User user) {
        Channel channel = new Channel();
        channel.setId(id);
        channel.setTitle(title);
        channel.setUser(user);
        return channel;
    }

    private void setId(Long id) {
        Objects.requireNonNull(id, "Channel id는 null이 될 수 없습니다.");
        this.id = id;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isNew() {
        return createdAt == null;
    }


}
