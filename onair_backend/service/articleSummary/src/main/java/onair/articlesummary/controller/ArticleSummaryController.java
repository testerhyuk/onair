package onair.articlesummary.controller;

import lombok.RequiredArgsConstructor;
import onair.articlesummary.service.ArticleSummaryService;
import onair.articlesummary.service.response.ArticleSummaryResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleSummaryController {
    private final ArticleSummaryService articleSummaryService;

    @GetMapping("/v1/article-summary/{articleId}")
    public ArticleSummaryResponse read(@PathVariable("articleId") Long articleId) {
        return articleSummaryService.read(articleId);
    }
}
