package demo.chatapp.channel.domain;

import demo.chatapp.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "entries")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "entryKey")
public class Entry {

    @EmbeddedId
    private EntryKey entryKey;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime joinedAt;

    public static Entry createEntry(Channel channel, User user) {
        EntryKey key = new EntryKey(channel, user);
        Entry entry = new Entry();
        entry.setEntryKey(key);
        return entry;
    }

    private void setEntryKey(EntryKey entryKey) {
        Objects.requireNonNull(entryKey, "EntryKey는 null이 될 수 없습니다.");
        this.entryKey = entryKey;
    }

}
