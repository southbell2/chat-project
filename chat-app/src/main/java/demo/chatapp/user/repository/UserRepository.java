package demo.chatapp.user.repository;

import demo.chatapp.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void saveUser(User user) {
        em.persist(user);
    }

}
