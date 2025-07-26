package onair.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.comment.service.reqeust.CommentCreateRequest;
import onair.comment.service.reqeust.CommentUpdateRequest;
import onair.comment.service.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

@Log4j2
public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9033");


    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, null, 136998378236133376L, "my content"));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, response1.getCommentId(), 136998378236133376L, "my content"));

        log.info("commentId = %s".formatted(response1.getCommentId()));
        log.info("commentId = %s".formatted(response2.getCommentId()));
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comment")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comment/{commentId}", 144159655048232960L)
                .retrieve()
                .body(CommentResponse.class);

        log.info("response = %s".formatted(response));
    }

    @Test
    void update() {
        CommentResponse response = updateComment(new CommentUpdateRequest("updated content"));

        log.info("response = %s".formatted(response));
    }

    CommentResponse updateComment(CommentUpdateRequest request) {
        return restClient.put()
                .uri("/v1/comment/{commentId}", 144159655048232960L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comment/{commentId}", 144159655710932992L)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> response1 = restClient.get()
                .uri("/v1/comment/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});

        log.info("firstPage");

        for (CommentResponse comment : response1) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                log.info("\t");
            }

            log.info("comment.getCommentId() = %s".formatted(comment.getCommentId()));
        }

        Long lastParentCommentId = response1.getLast().getParentCommentId();
        Long lastCommentId = response1.getLast().getCommentId();

        List<CommentResponse> response2 = restClient.get()
                .uri("/v1/comment/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                        .formatted(lastParentCommentId, lastCommentId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {});

        log.info("secondPage");

        for (CommentResponse comment : response2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                log.info("\t");
            }

            log.info("comment.getCommentId() = %s".formatted(comment.getCommentId()));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private Long parentCommentId;
        private Long userId;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentUpdateRequest {
        private String comment;
    }
}
