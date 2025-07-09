package onair.comment.service;

import lombok.RequiredArgsConstructor;
import onair.comment.entity.Comment;
import onair.comment.repository.CommentRepository;
import onair.comment.service.reqeust.CommentCreateRequest;
import onair.comment.service.reqeust.CommentUpdateRequest;
import onair.comment.service.response.CommentResponse;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final Snowflake snowflake = new Snowflake();

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Comment parent = findParent(request);

        Comment comment = commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        request.getArticleId(),
                        parent == null ? null : request.getParentCommentId(),
                        request.getUserId(),
                        request.getContent()
                )
        );

        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse update(Long commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        comment.update(request.getComment());

        return CommentResponse.from(comment);
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId) {
            commentRepository.findById(commentId)
                    .filter(not(Comment::isDeleted))
                    .ifPresent(comment -> {
                        if (hasChildren(comment)) {
                            comment.delete();
                        } else {
                            deleteComment(comment);
                        }
                    });
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getParentCommentId(), 2L) == 2;
    }

    private void deleteComment(Comment comment) {
        commentRepository.delete(comment);

        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                    .filter(Comment::isDeleted)
                    .filter(not(this::hasChildren))
                    .ifPresent(this::deleteComment);
        }
    }

    private Comment findParent(CommentCreateRequest request) {
        Long parentCommentId = request.getParentCommentId();

        if (parentCommentId == null) {
            return null;
        }

        return commentRepository.findById(parentCommentId)
                .filter(not(Comment::isDeleted))
                .filter(Comment::isRoot)
                .orElseThrow();
    }
}
