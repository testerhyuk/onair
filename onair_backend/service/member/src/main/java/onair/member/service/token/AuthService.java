package onair.member.service.token;

import lombok.RequiredArgsConstructor;
import onair.jwt.JwtProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public String reissueAccessToken(Long memberId, String refreshTokenFromClient) {
        String storedRefreshToken = refreshTokenService.getRefreshToken(memberId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshTokenFromClient)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        return jwtProvider.generateAccessToken(String.valueOf(memberId));
    }
}
