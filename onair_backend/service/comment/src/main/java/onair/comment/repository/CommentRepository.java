package onair.comment.repository;

import onair.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 하위 댓글 개수
    @Query(
            value = "select count(*) from (" +
                        "select comment_id from comment " +
                        "where article_id = :articleId and parent_comment_id = :parentCommentId " +
                        "limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long countBy(@Param("articleId") Long articleId,
                 @Param("parentCommentId") Long parentCommentId,
                 @Param("limit") Long limit
    );
}
