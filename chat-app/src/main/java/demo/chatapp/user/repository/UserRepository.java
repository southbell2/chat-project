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

    public User findByEmailWithRole(String email) {
        return em.createQuery(
                "SELECT distinct u FROM User u " +
                    "JOIN FETCH u.userRoles r " +
                    "WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .getSingleResult();
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

}
