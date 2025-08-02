package onair.hotArticle.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotArticleListRepositoryTest {
    @Autowired
    HotArticleListRepository hotArticleListRepository;

    @Test
    void addTest() throws InterruptedException {
        LocalDateTime time = LocalDateTime.of(2025, 7, 31, 0, 0);

        long limit = 3;

        hotArticleListRepository.addToRedis(1L, time, 2L, limit, Duration.ofSeconds(3));
        hotArticleListRepository.addToRedis(2L, time, 3L, limit, Duration.ofSeconds(3));
        hotArticleListRepository.addToRedis(3L, time, 1L, limit, Duration.ofSeconds(3));
        hotArticleListRepository.addToRedis(4L, time, 5L, limit, Duration.ofSeconds(3));
        hotArticleListRepository.addToRedis(5L, time, 4L, limit, Duration.ofSeconds(3));

        List<Long> articleIds = hotArticleListRepository.readAll("20250731");

        assertThat(articleIds).hasSize(Long.valueOf(limit).intValue());
        assertThat(articleIds.get(0)).isEqualTo(4);
        assertThat(articleIds.get(1)).isEqualTo(5);
        assertThat(articleIds.get(2)).isEqualTo(2);

        TimeUnit.SECONDS.sleep(5);

        assertThat(hotArticleListRepository.readAll("20250731")).isEmpty();
    }
}