package onair.member.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.member.entity.Member;
import onair.member.entity.Role;

@Getter
@ToString
public class LoginResponse {
    private String memberId;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Role role;

    public static LoginResponse of(Member member, String accessToken, String refreshToken) {
        LoginResponse response = new LoginResponse();

        response.memberId = String.valueOf(member.getMemberId());
        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;
        response.tokenType = "Bearer ";
        response.role = member.getRole();

        return response;
    }
}
