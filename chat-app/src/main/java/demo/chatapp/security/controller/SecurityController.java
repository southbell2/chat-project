package demo.chatapp.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken() {
        return ResponseEntity.ok().build();
    }
}
