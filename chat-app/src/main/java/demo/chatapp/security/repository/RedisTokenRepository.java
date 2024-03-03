package demo.chatapp.security.repository;

import demo.chatapp.security.token.RefreshToken;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveToken(RefreshToken refreshToken) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        hashOps.putAll(getTokenKey(refreshToken.getToken()), refreshTokenToMap(refreshToken));
    }

    @Override
    public Long getTokenExpiration(String token) {
        String key = getTokenKey(token);
        String exp = (String) redisTemplate.opsForHash().get(key, "exp");
        Objects.requireNonNull(exp, "String exp 객체는 null이면 안 됩니다.");
        return Long.parseLong(exp);
    }

    @Override
    public Map<String, String> getTokenInfo(String token) {
        return redisTemplate.<String, String>opsForHash().entries(getTokenKey(token));
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(getTokenKey(token));
    }

    @Override
    public boolean isTokenPresent(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getTokenKey(token)));
    }

    private String getTokenKey(String token) {
        return "token:" + token;
    }

    private Map<String, String> refreshTokenToMap(RefreshToken token) {
        return Map.of("id", String.valueOf(token.getUserId()),
            "email", token.getEmail(),
            "exp", String.valueOf(token.getExp()),
            "authorities", String.join(",", token.getAuthorities()));
    }
}
