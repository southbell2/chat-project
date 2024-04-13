package demo.chatapp.security.token;

import demo.chatapp.security.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccessTokenManager {

    private final String key;
    private final Long accessTokenValidityInMs;
    private SecretKey secretKey;

    @Autowired
    public AccessTokenManager(@Value("${jwt.secret-key}") String key,
        @Value("${jwt.token-validity-in-sec}") Long accessTokenValidity) {
        this.key = key;
        this.accessTokenValidityInMs = accessTokenValidity * 1000L;
    }

    @PostConstruct
    public void postConstruct() {
        secretKey = Keys.hmacShaKeyFor(
            key.getBytes(StandardCharsets.UTF_8));
    }

    public TokenStatus validateAccessToken(String jwt) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt);
            return TokenStatus.OK;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("JWT 검증 도중 예외 발생 e = {}", e.getClass());
            return TokenStatus.DENIED;
        }
    }

    public Authentication getAuthentication(String jwt) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(jwt)
            .getBody();

        Long id = Long.parseLong(claims.get("id").toString());
        String email = claims.getSubject();
        String authorities = claims.get("authorities").toString();
        return new UsernamePasswordAuthenticationToken(
            new UserPrincipal(id, email), null,
            AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
    }

    public String createAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityInMs);
        String authorities = populateAuthorities(authentication.getAuthorities());

        return Jwts.builder().setSubject(userPrincipal.getEmail())
            .claim("id", userPrincipal.getId())
            .claim("authorities", authorities)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(secretKey).compact();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    }


}
