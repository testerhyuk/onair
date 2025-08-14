package onair.member.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.member.entity.Member;
import onair.member.entity.Role;

@Getter
@ToString
public class LoginResponse {
    private Long memberId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Role role;

    public static LoginResponse of(Member member, String accessToken, String refreshToken) {
        LoginResponse response = new LoginResponse();

        response.memberId = member.getMemberId();
        response.email = member.getEmail();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;
        response.tokenType = "Bearer ";
        response.role = member.getRole();

        return response;
    }
}
