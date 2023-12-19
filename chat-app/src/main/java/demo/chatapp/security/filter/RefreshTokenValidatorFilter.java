package demo.chatapp.security.filter;

import static demo.chatapp.security.SecurityConstants.REFRESH_HEADER;

import demo.chatapp.security.token.RefreshTokenManager;
import demo.chatapp.security.token.TokenStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenValidatorFilter extends OncePerRequestFilter {

    private final RefreshTokenManager refreshTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String refreshToken = getRefTokenFromCookie(request);
        TokenStatus tokenStatus = refreshTokenManager.validateRefreshToken(refreshToken);

        if (tokenStatus == TokenStatus.OK) {
            Authentication authentication = refreshTokenManager.getAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getRefTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refToken = null;
        if (cookies != null) {
            refToken = Arrays.stream(cookies)
                .filter(cookie -> REFRESH_HEADER.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        }
        return refToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/refresh-token".equals(request.getRequestURI());
    }
}
