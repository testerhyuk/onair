package onair.comment.service;

import onair.comment.entity.Comment;
import onair.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("부모 댓글이 지워지지 않았으면 대댓글을 삭제했을 때 삭제 표시만")
    void markDeletedIfParentNotDeleted() {
        Long articleId = 1L;
        Long commentId = 2L;

        Comment comment = createComment(articleId, commentId);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(2L);

        commentService.delete(commentId);

        verify(comment).delete();
    }

    @Test
    @DisplayName("부모 댓글이 지워지지 않았을 때, 하위 댓글을 삭제하면 하위 댓글만 삭제됨")
    void deleteChildOnlyIfParentNotDeleted() {
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = mock(Comment.class);
        given(parentComment.isDeleted()).willReturn(false);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);
        given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));

        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parentComment);
    }

    @Test
    @DisplayName("하위 댓글도 삭제되고, 부모 댓글도 삭제된 상태면 재귀적으로 모두 삭제")
    void deleteAllRecursivelyIfParentDeleted() {
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = createComment(articleId, parentCommentId);
        given(parentComment.isRoot()).willReturn(true);
        given(parentComment.isDeleted()).willReturn(true);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);
        given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));
        given(commentRepository.countBy(articleId, parentCommentId, 2L)).willReturn(1L);

        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parentComment);
    }

    private Comment createComment(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);

        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);

        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = createComment(articleId, commentId);

        given(comment.getParentCommentId()).willReturn(parentCommentId);

        return comment;
    }
}