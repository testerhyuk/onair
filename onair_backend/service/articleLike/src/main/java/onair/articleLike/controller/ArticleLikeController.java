package onair.articleLike.controller;

import lombok.RequiredArgsConstructor;
import onair.articleLike.entity.ArticleLike;
import onair.articleLike.service.ArticleLikeService;
import onair.articleLike.service.response.ArticleLikeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {
    private final ArticleLikeService articleLikeService;

    @PostMapping("/v1/article-like/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        return articleLikeService.read(articleId, userId);
    }

    @PostMapping("/v1/article-like/articles/{articleId}/users/{userId}/like")
    public void like(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        articleLikeService.like(articleId, userId);
    }

    @DeleteMapping("/v1/article-like/articles/{articleId}/users/{userId}/unlike")
    public void unlike(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        articleLikeService.unlike(articleId, userId);
    }

    @GetMapping("/v1/article-like/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return articleLikeService.count(articleId);
    }

    @PostMapping("/v1/article-like/articles/{articleId}/users/{userId}/status")
    public Map<String, Boolean> getLikeStatus(
            @PathVariable Long articleId,
            @PathVariable Long userId
    ) {
        boolean likeStatus = articleLikeService.isLiked(articleId, userId);

        return Collections.singletonMap("likeStatus", likeStatus);
    }

    @GetMapping("/v1/article-like/articles/member/{userId}")
    public List<ArticleLikeResponse> getArticleLikeByUserId(@PathVariable("userId") String userId) {
        Long memberId = Long.parseLong(userId);
        return articleLikeService.getArticleLikeByUserId(memberId);
    }
}
