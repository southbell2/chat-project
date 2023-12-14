package demo.chatapp.user.controller;

import static demo.chatapp.security.SecurityConstants.REFRESH_HEADER;

import demo.chatapp.security.repository.TokenRepository;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.vo.SignUpUserRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenRepository tokenRepository;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpUserRequest userRequest) {
        userService.signUp(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = getRefTokenFromCookie(request);
        if (StringUtils.hasText(refreshToken)) {
            tokenRepository.deleteToken(refreshToken);
        }

        return ResponseEntity.ok().build();
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



}
