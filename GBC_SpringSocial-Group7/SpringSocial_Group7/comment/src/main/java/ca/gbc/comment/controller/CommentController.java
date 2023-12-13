package ca.gbc.comment.controller;

import ca.gbc.comment.dto.CommentRequest;
import ca.gbc.comment.dto.CommentResponse;
import ca.gbc.comment.service.CommentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "commentService", fallbackMethod = "placeCommentFallback")
    @TimeLimiter(name = "commentService")
    @Retry(name = "commentService")
    public CompletableFuture<ResponseEntity<String>> createComment(@RequestBody CommentRequest commentRequest) {
        return CompletableFuture.supplyAsync(() -> {
            commentService.createComment(commentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Comment Created Successfully");
        });
    }

    public CompletableFuture<ResponseEntity<String>> placeCommentFallback(CommentRequest request, Throwable e) {
        log.error("Exception is: {}", e.getMessage());
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("FALLBACK INVOKED: Comment Failed. Please try again later."));
    }

//    @GetMapping
//    public List<CommentResponse> getAllComments() {
//        return commentService.getAllComments();
//    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getAllPostComments(@PathVariable("postId") Long postId) {
        log.info("Retrieving all comments");
        return commentService.getCommentsForPost(postId);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        String updatedCommentId = commentService.updateComment(commentId, commentRequest);
        return updatedCommentId != null ?
                ResponseEntity.ok("Comment Updated Successfully") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment Not Found");
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
