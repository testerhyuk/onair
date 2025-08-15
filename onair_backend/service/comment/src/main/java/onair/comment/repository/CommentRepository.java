package onair.comment.repository;

import onair.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    // 첫 번째 스크롤 페이지
    @Query(
            value = "select comment.comment_id, comment.article_id, comment.parent_comment_id, comment.user_id, " +
                    "comment.content, comment.created_at, comment.modified_at, comment.deleted " +
                    "from comment " +
                    "where article_id = :articleId " +
                    "order by parent_comment_id asc, comment_id asc " +
                    "limit :limit",
            nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("limit") Long limit
    );

    // 두 번째 이상 스크롤 페이지
    @Query(
            value = "select comment.comment_id, comment.article_id, comment.parent_comment_id, comment.user_id, " +
                    "comment.content, comment.created_at, comment.modified_at, comment.deleted " +
                    "from comment " +
                    "where article_id = :articleId and (" +
                        "parent_comment_id > :lastParentCommentId or " +
                        "(parent_comment_id = :lastParentCommentId and comment_id > :lastCommentId) " +
                    ") order by parent_comment_id asc, comment_id asc limit :limit",
            nativeQuery = true
    )
    List<Comment> findAllInfiniteScroll(
            @Param("articleId") Long articleId,
            @Param("lastParentCommentId") Long lastParentCommentId,
            @Param("lastCommentId") Long lastCommentId,
            @Param("limit") Long limit
    );

    List<Comment> findAllByUserId(Long userId);
}
