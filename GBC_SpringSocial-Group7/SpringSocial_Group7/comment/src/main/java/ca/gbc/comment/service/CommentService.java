package ca.gbc.comment.service;

import ca.gbc.comment.dto.CommentRequest;
import ca.gbc.comment.dto.CommentResponse;

import java.util.List;

public interface CommentService {
    void createComment(CommentRequest commentRequest);

    String updateComment(Long commentId, CommentRequest commentRequest);

    void deleteComment(Long commentId);

//    List<CommentResponse> getAllComments();

    List<CommentResponse> getCommentsForPost(Long postId);
}
