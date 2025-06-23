package onair.article.repository;

import onair.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.created_at, article.modified_at " +
                    "where board_id = :boardId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllArticle(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.created_at, article.modified_at " +
                    "where board_id = :boardId and article_id < :lastArticleId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllArticle(@Param("boardId") Long boardId,
                                 @Param("limit") Long limit,
                                 @Param("lastArticleId") Long lastArticleId
    );
}
