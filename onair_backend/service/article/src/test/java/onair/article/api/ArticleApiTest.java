package onair.article.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import onair.article.service.ArticleService;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@Log4j2
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9030");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequestDto(
                1L, 1L, "test title", "test content"
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
                "update title", "update content"
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

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequestDto {
        private Long userId;
        private Long boardId;
        private String title;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequestDto {
        private String title;
        private String content;
    }
}
