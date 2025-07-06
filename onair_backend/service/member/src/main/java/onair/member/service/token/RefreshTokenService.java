package onair.member.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY_FORMAT = "member::%s::refreshToken";

    public void saveRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(generateKey(memberId), refreshToken, Duration.ofDays(7));
    }

    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get(generateKey(memberId));
    }

    public void deleteRefreshToken(Long memberId) {
        redisTemplate.delete(generateKey(memberId));
    }

    private String generateKey(Long memberId) {
        return KEY_FORMAT.formatted(memberId);
    }
}