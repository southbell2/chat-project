package demo.chatapp.security.filter;

import static demo.chatapp.security.SecurityConstants.ACCESS_HEADER;
import static demo.chatapp.security.token.TokenStatus.EXPIRED;
import static demo.chatapp.security.token.TokenStatus.OK;

import demo.chatapp.security.token.AccessTokenManager;
import demo.chatapp.security.token.TokenStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTokenValidatorFilter extends OncePerRequestFilter {

    private final AccessTokenManager accessTokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        TokenStatus accessTokenStatus = accessTokenManager.validateAccessToken(accessToken);

        if (accessTokenStatus == OK) {
            Authentication authentication = accessTokenManager.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (accessTokenStatus == EXPIRED) {
            response.setHeader("WWW-Authenticate",
                "error='token_expired', error_description='The access token expired'");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(ACCESS_HEADER);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
