package ca.gbc.commentservice.service;

import ca.gbc.commentservice.dto.CommentRequest;

import java.util.List;

public interface CommentService {
    void createComment(CommentRequest commentRequest);
    void deleteComment(String commentId);
    void updateComment(String commentId, CommentRequest commentRequest);
    List<CommentRequest> getAllComments();
}
