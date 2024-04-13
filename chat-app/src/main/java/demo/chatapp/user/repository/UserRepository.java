package demo.chatapp.user.repository;

import demo.chatapp.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT distinct u FROM User u JOIN FETCH u.userRoles r WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT distinct u FROM User u JOIN FETCH u.userRoles r WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") Long id);

    List<User> findByIdLessThan(Long id, Pageable pageable);

    void deleteById(Long id);
}
