package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Entry;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EntryRepository {

    private final EntityManager em;

    public void saveEntry(Entry entry) {
        em.persist(entry);
    }
}
