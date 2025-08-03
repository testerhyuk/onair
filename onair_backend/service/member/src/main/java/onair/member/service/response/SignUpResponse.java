package onair.member.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.member.entity.Member;

@Getter
@ToString
public class SignUpResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private String zipCode;
    private String address;
    private String detailAddress;
    private String role;

    public static SignUpResponse of(Member member) {
        SignUpResponse response = new SignUpResponse();

        response.memberId = member.getMemberId();
        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.zipCode = member.getZipCode();
        response.address = member.getAddress();
        response.detailAddress = member.getDetailAddress();
        response.role = String.valueOf(member.getRole());

        return response;
    }
}
