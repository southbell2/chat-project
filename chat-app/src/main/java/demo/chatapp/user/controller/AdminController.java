package demo.chatapp.user.controller;

import demo.chatapp.user.service.AdminService;
import demo.chatapp.user.service.UserService;
import demo.chatapp.user.service.dto.PagedUserResponse;
import demo.chatapp.user.service.dto.SignUpUserRequest;
import demo.chatapp.user.service.dto.UserInfoAdminResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @PostMapping("/admin/register-5f4dcc3b5aa765d61d8327deb882cf99")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpUserRequest userRequest) {
        adminService.signUpAdmin(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/admin/userinfo/{userId}")
    public ResponseEntity<UserInfoAdminResponse> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserInfoByAdmin(userId));
    }

    @GetMapping("/admin/userinfo-list")
    public ResponseEntity<List<PagedUserResponse>> getPagedUsers(@RequestParam(defaultValue = "0") long beforeId, @RequestParam(defaultValue = "10") int limit) {
        beforeId = convertIfDefaultId(beforeId);
        List<PagedUserResponse> pagedUsers = adminService.getPagedUsers(beforeId, limit);
        return ResponseEntity.ok(pagedUsers);
    }

    @DeleteMapping("/admin/delete-user")
    public ResponseEntity<Void> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private long convertIfDefaultId(long beforeId) {
        return (beforeId == 0) ? Long.MAX_VALUE : beforeId;
    }
}
