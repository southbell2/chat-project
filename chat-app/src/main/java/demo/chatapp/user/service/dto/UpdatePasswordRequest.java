package demo.chatapp.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {

    @NotBlank
    @Size(min = 4, max = 15, message = "비밀번호의 크기는 4~15 사이여야 합니다")
    private String nowPassword;

    @NotBlank
    @Size(min = 4, max = 15, message = "비밀번호의 크기는 4~15 사이여야 합니다")
    private String newPassword;

}
