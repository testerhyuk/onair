package onair.articleLike.api;

import lombok.extern.log4j.Log4j2;
import onair.articleLike.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class ArticleLikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9034");

    Long articleId = 132342059043856384L;
    Long userId = 136998378236133376L;

    @Test
    void likeTest() {
        like(articleId, userId);

        ArticleLikeResponse response = read(articleId, userId);

        log.info("response = %s".formatted(response));
    }

    @Test
    void unlikeTest() {
        unlike(articleId, userId);
    }

    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("/v1/article-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("/v1/article-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .toBodilessEntity();
    }

    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("/v1/article-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }
}
