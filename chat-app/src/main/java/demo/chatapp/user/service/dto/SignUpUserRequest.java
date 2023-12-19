package demo.chatapp.user.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpUserRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3, max = 10, message = "닉네임의 크기는 3~10 사이여야 합니다")
    private String nickname;

    @NotBlank
    @Size(min = 4, max = 15, message = "비밀번호의 크기는 4~15 사이여야 합니다")
    private String password;
}
