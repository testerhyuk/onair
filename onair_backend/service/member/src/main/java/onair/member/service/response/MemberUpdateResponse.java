package onair.member.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.member.entity.Member;

@Getter
@ToString
public class MemberUpdateResponse {
    private String email;
    private String nickname;
    private String zipCode;
    private String address;
    private String detailAddress;

    public static MemberUpdateResponse of(Member member) {
        MemberUpdateResponse response = new MemberUpdateResponse();

        response.email = member.getEmail();
        response.nickname = member.getNickname();
        response.zipCode = member.getZipCode();
        response.address = member.getAddress();
        response.detailAddress = member.getDetailAddress();

        return response;
    }
}
