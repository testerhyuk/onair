package onair.articleLike.service;

import lombok.RequiredArgsConstructor;
import onair.articleLike.entity.ArticleLike;
import onair.articleLike.entity.ArticleLikeCount;
import onair.articleLike.repository.ArticleLikeCountRepository;
import onair.articleLike.repository.ArticleLikeRepository;
import onair.articleLike.service.response.ArticleLikeResponse;
import onair.snowflake.Snowflake;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public void like(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));

        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlike(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    // 좋아요 기록 삭제
                    articleLikeRepository.delete(articleLike);

                    // like_count 감소
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository
                            .findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();

                    articleLikeCountRepository.save(articleLikeCount);
                });
    }

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId).map(ArticleLikeResponse::from).orElseThrow();
    }

    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }
}
