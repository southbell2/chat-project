package demo.chatapp.security.filter;

import static demo.chatapp.security.SecurityConstants.REFRESH_HEADER;

import demo.chatapp.security.SecurityConstants;
import demo.chatapp.security.token.AccessTokenManager;
import demo.chatapp.security.token.RefreshTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class TokenGeneratorFilter extends OncePerRequestFilter {

    private final AccessTokenManager accessTokenManager;
    private final RefreshTokenManager refreshTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String accessToken = accessTokenManager.createAccessToken(authentication);
            response.setHeader(SecurityConstants.ACCESS_HEADER,
                SecurityConstants.BEARER_TYPE + " " + accessToken);

            String oldRefToken = getRefTokenFromCookie(request);
            String newRefToken;
            if (oldRefToken == null) {
                newRefToken = refreshTokenManager.createRefreshToken(authentication);
            } else {
                newRefToken = refreshTokenManager.reIssueRefreshToken(oldRefToken, authentication);
            }
            refreshTokenManager.addRefTokenToCookie(response, newRefToken);
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
        String uri = request.getRequestURI();
        return !("/login".equals(uri) || "/refresh-token".equals(uri));
    }
}
