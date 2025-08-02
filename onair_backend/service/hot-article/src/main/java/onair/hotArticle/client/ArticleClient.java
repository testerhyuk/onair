package onair.hotArticle.client;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Log4j2
public class ArticleClient {
    private RestClient restClient;

    @Value("${endpoints.onair-article-service.url}")
    private String articleServiceUrl;

    @PostConstruct
    void initRestClient() {
        restClient = RestClient.create(articleServiceUrl);
    }

    public ArticleResponse read(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v1/article/{articleId}", articleId)
                    .retrieve()
                    .body(ArticleResponse.class);
        } catch (Exception e) {
            log.error("[ArticleClient.read] articleId = {}", articleId, e);
        }

        return null;
    }

    @Getter
    public static class ArticleResponse {
        private Long articleId;
        private String title;
        private LocalDateTime createdAt;
    }
}
