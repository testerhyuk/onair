package onair.hotArticle.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.random.RandomGenerator;

public class DataInitializer {
    RestClient articleServiceClient = RestClient.create("http://localhost:9030");
    RestClient commentServiceClient = RestClient.create("http://localhost:9033");
    RestClient likeServiceClient = RestClient.create("http://localhost:9034");
    RestClient viewServiceClient = RestClient.create("http://localhost:9035");

    @Test
    void initialize() {
        for(int i=0; i<30; i++) {
            Long articleId = createArticle();
            long commentCount = RandomGenerator.getDefault().nextLong(10);
            long likeCount = RandomGenerator.getDefault().nextLong(10);
            long viewCount = RandomGenerator.getDefault().nextLong(200);

            createComment(articleId, commentCount);
            like(articleId, likeCount);
            view(articleId, viewCount);
        }
    }

    Long createArticle() {
        return articleServiceClient.post()
                .uri("/v1/article")
                .body(new ArticleCreateRequest(1L, 1L, "title", "content"))
                .retrieve()
                .body(ArticleResponse.class)
                .getArticleId();
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private Long boardId;
        private Long userId;
        private String title;
        private String content;
    }

    @Getter
    static class ArticleResponse {
        private Long articleId;
    }

    void createComment(Long articleId, long commentCount) {
        while(commentCount-- > 0) {
            commentServiceClient.post()
                    .uri("/v1/comment")
                    .body(new CommentCreateRequest(articleId, "content", 1L))
                    .retrieve()
                    .body(String.class);
        }
    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long userId;
    }

    void like(Long articleId, long likeCount) {
        while(likeCount-- > 0) {
            likeServiceClient.post()
                    .uri("/v1/article-like/articles/{articleId}/users/{userId}", articleId, likeCount)
                    .retrieve()
                    .body(String.class);
        }
    }

    void view(Long articleId, long viewCount) {
        while(viewCount-- > 0) {
            viewServiceClient.post()
                    .uri("/v1/article-views/articles/{articleId}/users/{userId}/count", articleId, viewCount)
                    .retrieve()
                    .body(String.class);
        }
    }
}
