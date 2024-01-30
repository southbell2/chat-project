package demo.chatapp.user.controller;

import static demo.chatapp.security.SecurityConstants.REFRESH_HEADER;

import demo.chatapp.security.principal.UserPrincipal;
import demo.chatapp.security.repository.TokenRepository;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UpdatePasswordRequest;
import demo.chatapp.user.service.dto.UpdateUserInfoRequest;
import demo.chatapp.user.service.dto.UserInfoResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/userinfo")
    public ResponseEntity<UserInfoResponse> userInfo(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfo);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<Void> deleteUser(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-userinfo")
    public ResponseEntity<Void> updateUserInfo(@RequestBody @Valid UpdateUserInfoRequest userInfoRequest,
        Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        userService.updateUserInfo(userId, userInfoRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(Authentication authentication, @RequestBody @Valid
    UpdatePasswordRequest updatePasswordRequest) {
        Long userId = getUserIdFromAuthentication(authentication);
        userService.updatePassword(userId, updatePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
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
