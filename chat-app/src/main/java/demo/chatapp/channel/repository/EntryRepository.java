package demo.chatapp.channel.repository;

import demo.chatapp.channel.domain.Entry;
import demo.chatapp.channel.domain.EntryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, EntryKey> {

}
