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

    public User findByIdWithRole(Long id) {
        return em.createQuery(
                "SELECT distinct u FROM User u " +
                    "JOIN FETCH u.userRoles r " +
                    "WHERE u.id = :id", User.class)
            .setParameter("id", id)
            .getSingleResult();
    }

    public void deleteUser(User user) {
        em.createQuery("DELETE FROM User u WHERE u.id = :id")
            .setParameter("id", user.getId())
            .executeUpdate();
    }

}
