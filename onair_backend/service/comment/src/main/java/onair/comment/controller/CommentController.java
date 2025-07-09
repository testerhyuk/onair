package onair.comment.controller;

import lombok.RequiredArgsConstructor;
import onair.comment.service.CommentService;
import onair.comment.service.reqeust.CommentCreateRequest;
import onair.comment.service.reqeust.CommentUpdateRequest;
import onair.comment.service.response.CommentResponse;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/v1/comment/{commentId}")
    public CommentResponse update(@PathVariable("commentId") Long commentId, @RequestBody CommentUpdateRequest request) {
        return commentService.update(commentId, request);
    }

    @DeleteMapping("/v1/comment/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }
}
