package onair.member.service.token;

import lombok.RequiredArgsConstructor;
import onair.jwt.JwtProvider;
import onair.member.entity.Member;
import onair.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    public String reissueAccessToken(Long memberId, String refreshTokenFromClient) {
        String storedRefreshToken = refreshTokenService.getRefreshToken(memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshTokenFromClient)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        Member member = memberRepository.findById(memberId).orElseThrow();

        return jwtProvider.generateAccessToken(String.valueOf(memberId), member.getEmail(), member.getRole().name());
    }
}
