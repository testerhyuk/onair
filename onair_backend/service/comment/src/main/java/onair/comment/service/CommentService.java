package onair.comment.service;

import lombok.RequiredArgsConstructor;
import onair.comment.entity.Comment;
import onair.comment.entity.CommentCount;
import onair.comment.repository.CommentCountRepository;
import onair.comment.repository.CommentRepository;
import onair.comment.service.reqeust.CommentCreateRequest;
import onair.comment.service.reqeust.CommentUpdateRequest;
import onair.comment.service.response.CommentResponse;
import onair.event.EventType;
import onair.event.payload.ArticleCreatedEventPayload;
import onair.event.payload.CommentCreatedEventPayload;
import onair.event.payload.CommentDeletedEventPayload;
import onair.outboxmessagerelay.OutboxEventPublisher;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentCountRepository commentCountRepository;
    private final Snowflake snowflake = new Snowflake();
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Comment parent = findParent(request);

        Comment comment = commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        request.getArticleId(),
                        parent == null ? null : parent.getCommentId(),
                        request.getUserId(),
                        request.getContent()
                )
        );

        int result = commentCountRepository.increase(request.getArticleId());

        if (result == 0) {
            commentCountRepository.save(
                    CommentCount.init(request.getArticleId(), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.COMMENT_CREATED,
                CommentCreatedEventPayload.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .articleId(comment.getArticleId())
                        .userId(comment.getUserId())
                        .createdAt(comment.getCreatedAt())
                        .deleted(comment.isDeleted())
                        .commentCount(count(comment.getArticleId()))
                        .build(),
                comment.getArticleId()
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

                        outboxEventPublisher.publish(
                                EventType.COMMENT_DELETED,
                                CommentDeletedEventPayload.builder()
                                        .commentId(comment.getCommentId())
                                        .content(comment.getContent())
                                        .articleId(comment.getArticleId())
                                        .userId(comment.getUserId())
                                        .createdAt(comment.getCreatedAt())
                                        .deleted(comment.isDeleted())
                                        .commentCount(count(comment.getArticleId()))
                                        .build(),
                                comment.getArticleId()
                        );
                    });
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    private void deleteComment(Comment comment) {
        commentRepository.delete(comment);

        commentCountRepository.decrease(comment.getArticleId());

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

    public List<CommentResponse> readAll(Long articleId, Long lastParentCommentId, Long lastCommentId, Long limit) {
        List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
                commentRepository.findAllInfiniteScroll(articleId, limit) :
                commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);

        return comments.stream().map(CommentResponse::from).toList();
    }

    public Long count(Long articleId) {
        return commentCountRepository.findById(articleId)
                .map(CommentCount::getArticleCommentCount)
                .orElse(0L);
    }
}
