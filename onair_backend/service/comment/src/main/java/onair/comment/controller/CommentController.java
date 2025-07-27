package onair.comment.controller;

import lombok.RequiredArgsConstructor;
import onair.comment.service.CommentService;
import onair.comment.service.reqeust.CommentCreateRequest;
import onair.comment.service.reqeust.CommentUpdateRequest;
import onair.comment.service.response.CommentResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/v1/comment/{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId) {
        return commentService.read(commentId);
    }

    @PostMapping("/v1/comment")
    public CommentResponse create(@RequestBody CommentCreateRequest request) {
        return commentService.create(request);
    }

    @PutMapping("/v1/comment/{commentId}")
    public CommentResponse update(@PathVariable("commentId") Long commentId, @RequestBody CommentUpdateRequest request) {
        return commentService.update(commentId, request);
    }

    @DeleteMapping("/v1/comment/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }

    @GetMapping("/v1/comment/infinite-scroll")
    public List<CommentResponse> readAll(
            @RequestParam("articleId") Long articleId,
            @RequestParam(value = "lastParentCommentId", required = false) Long lastParentCommentId,
            @RequestParam(value = "lastCommentId", required = false) Long lastCommentId,
            @RequestParam("pageSize") Long pageSize
    ) {
        return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
    }

    @GetMapping("/v1/comment/articles/{articleId}/count")
    public Long count(@PathVariable("articleId") Long articleId) {
        return commentService.count(articleId);
    }
}
