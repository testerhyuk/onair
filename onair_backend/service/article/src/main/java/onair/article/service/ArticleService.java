package onair.article.service;

import lombok.RequiredArgsConstructor;
import onair.article.entity.Article;
import onair.article.repository.ArticleRepository;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public ArticleResponse create(ArticleCreateRequestDto dto) {
        Article article = articleRepository.save(Article.create(
                snowflake.nextId(),
                dto.getBoardId(),
                dto.getUserId(),
                dto.getTitle(),
                dto.getContent()
                )
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequestDto dto) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(dto.getTitle(), dto.getContent());

        return ArticleResponse.from(article);
    }

    @Transactional
    public void delete(Long articleId) {
        articleRepository.deleteById(articleId);
    }

    public ArticleResponse read(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        return ArticleResponse.from(article);
    }

    public List<ArticleResponse> readAll(Long boardId, Long limit, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllArticle(boardId, limit) :
                articleRepository.findAllArticle(boardId, limit, lastArticleId);

        return articles.stream().map(ArticleResponse::from).toList();
    }
}
