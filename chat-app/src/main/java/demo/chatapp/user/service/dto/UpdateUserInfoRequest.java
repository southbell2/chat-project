package demo.chatapp.user.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserInfoRequest {

    @NotBlank
    @Size(min = 3, max = 10, message = "닉네임의 크기는 3~10 사이여야 합니다")
    private String nickname;

}
