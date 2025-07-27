package onair.article.controller;

import lombok.RequiredArgsConstructor;
import onair.article.service.ArticleService;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/v1/article")
    public ArticleResponse create(@RequestBody ArticleCreateRequestDto dto) {
        return articleService.create(dto);
    }

    @PutMapping("/v1/article/{articleId}")
    public ArticleResponse update(@PathVariable("articleId") Long articleId,
                                  @RequestBody ArticleUpdateRequestDto dto) {
        return articleService.update(articleId, dto);
    }

    @DeleteMapping("/v1/article/{articleId}")
    public void delete(@PathVariable("articleId") Long articleId) {
        articleService.delete(articleId);
    }

    @GetMapping("/v1/article/{articleId}")
    public ArticleResponse read(@PathVariable("articleId") Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/v1/article/article-list")
    public List<ArticleResponse> readAll(@RequestParam("boardId") Long boardId,
                                         @RequestParam("pageSize") Long pageSize,
                                         @RequestParam(value = "lastArticleId", required = false) Long lastArticleId) {
        return articleService.readAll(boardId, pageSize, lastArticleId);
    }

    @GetMapping("/v1/article/boards/{boardId}/count")
    public Long count(@PathVariable("boardId") Long boardId) {
        return articleService.count(boardId);
    }
}
