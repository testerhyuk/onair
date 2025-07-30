package onair.articleLike.repository;

import jakarta.persistence.LockModeType;
import onair.articleLike.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ArticleLikeCountRepository extends JpaRepository<ArticleLikeCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ArticleLikeCount> findLockedByArticleId(Long articleId);
}
