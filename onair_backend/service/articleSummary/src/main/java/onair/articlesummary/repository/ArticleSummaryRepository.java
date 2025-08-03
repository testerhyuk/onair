package onair.articlesummary.repository;

import onair.articlesummary.entity.ArticleSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleSummaryRepository extends JpaRepository<ArticleSummary, Long> {
    Optional<ArticleSummary> findByArticleId(Long articleId);
}
