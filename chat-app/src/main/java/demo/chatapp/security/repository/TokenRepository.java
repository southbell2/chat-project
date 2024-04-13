package demo.chatapp.security.repository;

import demo.chatapp.security.token.RefreshToken;
import java.util.Map;

public interface TokenRepository {

    void saveToken(RefreshToken refreshToken);

    Long getTokenExpiration(String token);

    Map<String, String> getTokenInfo(String token);

    void deleteToken(String token);

    boolean isTokenPresent(String token);
}
