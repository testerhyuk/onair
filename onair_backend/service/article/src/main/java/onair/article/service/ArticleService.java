package onair.article.service;

import lombok.RequiredArgsConstructor;
import onair.article.entity.Article;
import onair.article.entity.BoardArticleCount;
import onair.article.entity.Category;
import onair.article.repository.ArticleRepository;
import onair.article.repository.BoardArticleCountRepository;
import onair.article.service.request.ArticleCreateRequestDto;
import onair.article.service.request.ArticleUpdateRequestDto;
import onair.article.service.response.ArticleResponse;
import onair.event.EventType;
import onair.event.payload.ArticleCreatedEventPayload;
import onair.event.payload.ArticleDeletedEventPayload;
import onair.event.payload.ArticleUpdatedEventPayload;
import onair.outboxmessagerelay.OutboxEventPublisher;
import onair.snowflake.Snowflake;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final Snowflake snowflake = new Snowflake();
    private final OutboxEventPublisher outboxEventPublisher;

    @Retryable(
            retryFor = {CannotAcquireLockException.class, PessimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public ArticleResponse create(ArticleCreateRequestDto dto) {
        Article article = articleRepository.save(Article.create(
                snowflake.nextId(),
                dto.getBoardId(),
                dto.getUserId(),
                dto.getTitle(),
                dto.getContent(),
                Category.valueOf(dto.getCategory())
                )
        );

        BoardArticleCount boardArticleCount = boardArticleCountRepository.findLockedByBoardId(dto.getBoardId())
                .orElseGet(() -> BoardArticleCount.init(dto.getBoardId(), 0L));

        boardArticleCount.increase();
        boardArticleCountRepository.save(boardArticleCount);

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .userId(article.getUserId())
                        .category(String.valueOf(article.getCategory()))
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .articleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequestDto dto) {
        Article article = articleRepository.findById(articleId).orElseThrow();

        article.update(dto.getTitle(), dto.getContent(), Category.valueOf(dto.getCategory()));

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .userId(article.getUserId())
                        .category(String.valueOf(article.getCategory()))
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);

        boardArticleCountRepository.findLockedByBoardId(article.getBoardId())
                .ifPresent(boardArticleCount -> {
                    boardArticleCount.decrease();
                    boardArticleCountRepository.save(boardArticleCount);
                });

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .userId(article.getUserId())
                        .createdAt(article.getCreatedAt())
                        .category(String.valueOf(article.getCategory()))
                        .modifiedAt(article.getModifiedAt())
                        .articleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );
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

    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }

    public List<ArticleResponse> search(Category category, String title) {
        List<Article> result = articleRepository.findAllByCategoryAndTitleContaining(category, title);

        return result.stream().map(ArticleResponse::from).toList();
    }

    public List<ArticleResponse> search(String title) {
        List<Article> result = articleRepository.findAllByTitleContaining(title);

        return result.stream().map(ArticleResponse::from).toList();
    }

    public List<ArticleResponse> readAllByCategory(Category category) {
        List<Article> result = articleRepository.findAllByCategory(category);

        return result.stream().map(ArticleResponse::from).toList();
    }
}
