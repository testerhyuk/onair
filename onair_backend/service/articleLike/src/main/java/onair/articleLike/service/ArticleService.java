package onair.articleLike.service;

import lombok.RequiredArgsConstructor;
import onair.articleLike.entity.ArticleLike;
import onair.articleLike.repository.ArticleLikeRepository;
import onair.articleLike.service.response.ArticleLikeResponse;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleLikeRepository articleLikeRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public void like(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId).ifPresent(like -> {
                    throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        });

        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
    }

    @Transactional
    public void unlike(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId).ifPresent(articleLikeRepository::delete);
    }

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId).map(ArticleLikeResponse::from).orElseThrow();
    }
}
