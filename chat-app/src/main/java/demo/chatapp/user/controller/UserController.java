package demo.chatapp.user.controller;

import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.vo.SignUpUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpUserRequest userRequest) {
        userService.signUp(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
