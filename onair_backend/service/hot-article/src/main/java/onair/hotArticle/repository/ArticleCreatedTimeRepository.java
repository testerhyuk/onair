package onair.hotArticle.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Repository
public class ArticleCreatedTimeRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "hot-article::article::%s::created-time";

    public void createOrUpdateCreatedTime(Long articleId, LocalDateTime createdAt, Duration ttl) {
        redisTemplate.opsForValue().set(
                generateKey(articleId),
                String.valueOf(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli()),
                ttl
        );
    }

    public void delete(Long articleId) {
        redisTemplate.delete(generateKey(articleId));
    }

    public LocalDateTime read(Long articleId) {
        String result = redisTemplate.opsForValue().get(generateKey(articleId));

        if (result == null) {
            return null;
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(result)), ZoneOffset.UTC
        );
    }

    private String generateKey(Long articleId) {
        return KEY_FORMAT.formatted(articleId);
    }
}
