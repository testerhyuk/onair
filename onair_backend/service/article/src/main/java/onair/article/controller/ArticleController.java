package onair.article.controller;

import lombok.RequiredArgsConstructor;
import onair.article.entity.Category;
import onair.article.service.ArticleService;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping("/v1/article")
    public ArticleResponse create(
            @RequestBody ArticleCreateRequestDto dto,
            @RequestHeader("X-Member-Role") String role) throws AccessDeniedException {

        if (!"REPORTER".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("게시글 작성 권한이 없습니다.");
        }

        return articleService.create(dto);
    }

    @PutMapping("/v1/article/{articleId}")
    public ArticleResponse update(@PathVariable("articleId") Long articleId,
                                  @RequestBody ArticleUpdateRequestDto dto,
                                  @RequestHeader("X-Member-Role") String role) throws AccessDeniedException {

        if (!"REPORTER".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("게시글 작성 권한이 없습니다.");
        }

        return articleService.update(articleId, dto);
    }

    @DeleteMapping("/v1/article/{articleId}")
    public void delete(@PathVariable("articleId") Long articleId,
                       @RequestHeader("X-Member-Role") String role) throws AccessDeniedException {

        if (!"REPORTER".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("게시글 작성 권한이 없습니다.");
        }

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

    @GetMapping("/v1/article/category")
    public List<ArticleResponse> readAllByCategory(@RequestParam("boardId") Long boardId,
                                                   @RequestParam("category") String category,
                                                   @RequestParam("pageSize") Long pageSize,
                                                   @RequestParam(value = "lastArticleId", required = false) Long lastArticleId
                                                   ) {
        return articleService.readAllByCategory(boardId, category, pageSize, lastArticleId);
    }

    @GetMapping("/v1/article/search")
    public List<ArticleResponse> search(
            @RequestParam("boardId") Long boardId,
            @RequestParam("keyword") String keyword,
            @RequestParam("pageSize") Long pageSize,
            @RequestParam(value = "lastArticleId", required = false) Long lastArticleId) {

        return articleService.search(boardId, keyword, pageSize, lastArticleId);
    }
}
