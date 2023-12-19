package demo.chatapp.user.domain;

import demo.chatapp.exception.BadRequestException;
import demo.chatapp.exception.UnauthorizedException;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UpdateUserInfoRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(columnDefinition = "VARCHAR(50) NOT NULL UNIQUE")
    private String email;

    @Column(columnDefinition = "VARCHAR(100) NOT NULL")
    private String password;

    @Column(columnDefinition = "CHAR(10) NOT NULL UNIQUE")
    private String nickname;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @BatchSize(size = 5)
    private List<UserRole> userRoles = new ArrayList<>();

    public static User createUser(SignUpUserRequest signUpUser, PasswordEncoder passwordEncoder, UserRole... userRoles) {
        User user = new User();
        user.setEmail(signUpUser.getEmail());
        user.setNickname(signUpUser.getNickname());
        user.setPassword(signUpUser.getPassword(), passwordEncoder);
        Arrays.stream(userRoles).forEach(user::addUserRole);

        return user;
    }

    public void addUserRole(UserRole userRole) {
        if (userRoles.stream().noneMatch(u -> u.getRole().equals(userRole.getRole()))) {
            userRoles.add(userRole);
            userRole.setUser(this);
        }
    }

    public void updateUserInfo(UpdateUserInfoRequest userInfoRequest) {
        setNickname(userInfoRequest.getNickname());
    }

    public void updatePassword(String newPassword, String nowPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(nowPassword, this.password)) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }
        setPassword(newPassword, passwordEncoder);
    }

    private void setPassword(String password, PasswordEncoder passwordEncoder) {
        if (password == null || password.length() < 4 || 15 < password.length()) {
            throw new BadRequestException("비밀번호가 적절하지 않습니다.");
        }
        this.password = passwordEncoder.encode(password);
    }

    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void setEmail(String email) {
        this.email = email;
    }



}
