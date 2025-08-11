package onair.article.repository;

import onair.article.entity.Article;
import onair.article.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllArticle(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllArticle(@Param("boardId") Long boardId,
                                 @Param("limit") Long limit,
                                 @Param("lastArticleId") Long lastArticleId
    );

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and category = :category " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByCategory(@Param("boardId") Long boardId,
                                    @Param("category") String category,
                                    @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId and category = :category " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByCategory(@Param("boardId") Long boardId,
                                    @Param("category") String category,
                                    @Param("limit") Long limit,
                                    @Param("lastArticleId") Long lastArticleId);

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and (title like :keyword or content like :keyword) " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByTitleAndContentContaining(@Param("boardId") Long boardId,
                                                     @Param("keyword") String keyword,
                                                     @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.user_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId and " +
                    "(title like :keyword or content like :keyword) " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByTitleAndContentContaining(@Param("boardId") Long boardId,
                                                     @Param("keyword") String keyword,
                                                     @Param("limit") Long limit,
                                                     @Param("lastArticleId") Long lastArticleId);
}
