package onair.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    private Long memberId;
    private String email;
    private String password;
    private String nickname;
    private String zipCode;
    private String address;
    private String detailAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean deleted;

    public static Member create(Long memberId, String email, String password, String nickname, String zipCode,
                                String address, String detailAddress) {
        Member member = new Member();

        member.memberId = memberId;
        member.email = email;
        member.password = password;
        member.nickname = nickname;
        member.zipCode = zipCode;
        member.address = address;
        member.detailAddress = detailAddress;
        member.createdAt = LocalDateTime.now();
        member.updatedAt = member.createdAt;
        member.deleted = false;

        return member;
    }

    public void update(String password, String nickname, String zipCode, String address, String detailAddress) {
        if (password != null) this.password = password;
        if (nickname != null) this.nickname = nickname;
        if (zipCode != null) this.zipCode = zipCode;
        if (address != null) this.address = address;
        if (detailAddress != null) this.detailAddress = detailAddress;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
