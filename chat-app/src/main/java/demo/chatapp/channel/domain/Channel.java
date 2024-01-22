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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "channels")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel {

    @Id
    @Column(name = "channel_id")
    private Long id;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "VARCHAR(30) NOT NULL")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User user;

    @OneToMany(mappedBy = "channel")
    private List<Entry> entries = new ArrayList<>();

    public static Channel createChannel(long id, String title, User user) {
        Channel channel = new Channel();
        channel.setId(id);
        channel.setTitle(title);
        channel.setUser(user);
        return channel;
    }

    private void setId(long id) {
        this.id = id;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setUser(User user) {
        this.user = user;
    }

}
