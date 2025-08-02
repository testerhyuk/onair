package onair.hotArticle.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Log4j2
public class HotArticleListRepository {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_FORMAT = "hot-article::list::%s";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void addToRedis(Long articleId, LocalDateTime time, Long score, Long limit, Duration ttl) {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(time);

            conn.zAdd(key, score, String.valueOf(articleId));
            conn.zRemRange(key, 0, - limit - 1);
            conn.expire(key, ttl.toSeconds());

            return null;
        });
    }

    public List<Long> readAll(String dateStr) {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(generateKey(dateStr), 0, -1).stream()
                .peek(tuple ->
                        log.info("[HotArticleListRepository.readAll] articleId = {}, score = {}",
                                tuple.getValue(), tuple.getScore()))
                .map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull)
                .map(Long::valueOf)
                .toList();
    }

    public void remove(Long articleId, LocalDateTime time) {
        redisTemplate.opsForZSet().remove(generateKey(time), String.valueOf(articleId));
    }

    private String generateKey(LocalDateTime time) {
        return generateKey(TIME_FORMATTER.format(time));
    }

    private String generateKey(String dateStr) {
        return KEY_FORMAT.formatted(dateStr);
    }
}
