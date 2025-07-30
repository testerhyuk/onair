package onair.articleLike.controller;

import lombok.RequiredArgsConstructor;
import onair.articleLike.service.ArticleLikeService;
import onair.articleLike.service.response.ArticleLikeResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {
    private final ArticleLikeService articleService;

    @GetMapping("/v1/article-like/articles/{articleId}/users/{userId}")
    public ArticleLikeResponse read(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        return articleService.read(articleId, userId);
    }

    @PostMapping("/v1/article-like/articles/{articleId}/users/{userId}")
    public void like(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        articleService.like(articleId, userId);
    }

    @DeleteMapping("/v1/article-like/articles/{articleId}/users/{userId}")
    public void unlike(
            @PathVariable("articleId") Long articleId,
            @PathVariable("userId") Long userId
    ) {
        articleService.unlike(articleId, userId);
    }

    @GetMapping("/v1/article-like/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return articleService.count(articleId);
    }
}
