package onair.member.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(
            regexp = "^[A-Za-z가-힣][A-Za-z가-힣0-9]{2,14}$",
            message = "숫자와 영문자, 한글만 사용할 수 있고, 숫자는 앞에 올 수 없으며 3~15자여야 합니다."
    )
    private String nickname;


    private String zipCode;
    private String address;
    private String detailAddress;
}
