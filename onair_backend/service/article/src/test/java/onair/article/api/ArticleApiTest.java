package onair.article.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import onair.article.entity.Category;
import onair.article.service.ArticleService;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9030");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequestDto(
                1L, 1L, "test title with category", "test content with category", "ELECTRONICS"
        ));

        log.info("response = {}", response);
    }

    ArticleResponse create(ArticleCreateRequestDto dto) {
        return restClient.post()
                .uri("/v1/article")
                .body(dto)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        Long articleId = 132271061575491584L;
        ArticleResponse response = update(articleId, new ArticleUpdateRequestDto(
                "update title", "update content", "ETC"
        ));

        log.info("response = {}", response);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequestDto dto) {
        return restClient.put()
                .uri("/v1/article/{articleId}", articleId)
                .body(dto)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        Long articleId = 132271061575491584L;

        ArticleResponse response = read(articleId);

        log.info("response = {}", response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/article/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest() {
        Long articleId = 132271061575491584L;

        restClient.delete()
                .uri("/v1/article/{articleId}", articleId)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void findAllTest() {
        List<ArticleResponse> firstPage = restClient.get()
                .uri("/v1/article/article-list&boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        log.info("first page");

        for (ArticleResponse articleResponse : firstPage) {
            log.info("first page data = {}", articleResponse.getArticleId());
        }

        Long lastArticleId = firstPage.getLast().getArticleId();

        List<ArticleResponse> nextPage = restClient.get()
                .uri("/v1/article/article-list&boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        for (ArticleResponse articleResponse : nextPage) {
            log.info("next page data = {}", articleResponse.getArticleId());
        }
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequestDto(1L, 2L, "title1", "content1", "ELECTRONICS"));

        Long count1 = restClient.get()
                .uri("/v1/article/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);

        log.info("count1 = %s".formatted(count1));

        restClient.delete()
                .uri("/v1/article/{articleId}", response.getArticleId())
                .retrieve()
                .body(ArticleResponse.class);

        Long count2 = restClient.get()
                .uri("/v1/article/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);

        log.info("count2 = %s".formatted(count2));
    }

    @Test
    void concurrencyCountTest() throws InterruptedException {
        Long boardId = 120L;
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (1000 + i);

            executorService.execute(() -> {
                try {
                    create(new ArticleCreateRequestDto(userId, boardId, "concurrency test", "concurrency content", "ELECTRONICS"));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Long articleCount = restClient.get()
                .uri("/v1/article/boards/{boardId}/count", boardId)
                .retrieve()
                .body(Long.class);

        log.info("final articleCount = %s".formatted(articleCount));

        assertThat(articleCount).isEqualTo(threadCount);
    }

    @Test
    void readByCategory() {
        List<ArticleResponse> responses = restClient.get()
                .uri("/v1/article/category?category=ELECTRONICS")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        for (ArticleResponse response : responses) {
            log.info("response = %s".formatted(response));
        }
    }

    @Test
    void searchAll() {
        List<ArticleResponse> responses = restClient.get()
                .uri("/v1/article/search?keyword=test")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        for (ArticleResponse response : responses) {
            log.info("response = %s".formatted(response));
        }
    }

    @Test
    void searchCategoryAndTitle() {
        List<ArticleResponse> responses = restClient.get()
                .uri("/v1/article/search?category=ELECTRONICS&keyword=test")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        for (ArticleResponse response : responses) {
            log.info("response = %s".formatted(response));
        }
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequestDto {
        private Long userId;
        private Long boardId;
        private String title;
        private String content;
        private String category;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequestDto {
        private String title;
        private String content;
        private String category;
    }
}
