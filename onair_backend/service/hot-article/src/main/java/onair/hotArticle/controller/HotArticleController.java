package onair.hotArticle.controller;

import lombok.RequiredArgsConstructor;
import onair.hotArticle.service.HotArticleService;
import onair.hotArticle.service.response.HotArticleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HotArticleController {
    private final HotArticleService hotArticleService;

    @GetMapping("/v1/hot-articles/article/date/{dateStr}")
    public List<HotArticleResponse> readAll(@PathVariable("dateStr") String dateStr) {
        return hotArticleService.readAll(dateStr);
    }
}
