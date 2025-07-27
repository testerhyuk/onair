package onair.comment.repository;

import onair.comment.entity.CommentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentCountRepository extends JpaRepository<CommentCount, Long> {
    @Query(
            value = "update comment_count set article_comment_count = article_comment_count + 1 where article_id = :articleId",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("articleId") Long articleId);

    @Query(
            value = "update comment_count set article_comment_count = article_comment_count - 1 where article_id = :articleId",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("articleId") Long articleId);
}
