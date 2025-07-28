package onair.articleView.repository;

import onair.articleView.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleViewBackUpRepository extends JpaRepository<ArticleView, Long> {
    @Query(
            value = "update article_view set view_count = :viewCount " +
                    "where article_id = :articleId and view_count < :viewCount",
            nativeQuery = true
    )
    @Modifying
    int updateViewCount(@Param("articleId") Long articleId, @Param("viewCount") Long viewCount);
}
