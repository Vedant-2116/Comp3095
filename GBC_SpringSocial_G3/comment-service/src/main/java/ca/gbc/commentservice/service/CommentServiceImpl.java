package ca.gbc.commentservice.service;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl {
    private final CommentRepository commentRepository;
    private final MongoTemplate mongoTemplate;

    public void createComment(CommentRequest commentRequest) {
        log.info("Creating A New Comment{}", commentRequest.getContent());
        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .build();
        commentRepository.save(comment);
        log.info("Comment {} Is saved", comment.getId());
    }

    public String updateComment(String commentId,CommentRequest commentRequest) {
        log.info("Updating A Comment With Id {}", commentId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(commentId));
        Comment comment = mongoTemplate.findOne(query, Comment.class);

        if(comment != null) {
            comment.setContent(commentRequest.getContent());

            log.info("Comment {} Is Updated", comment.getId());
            return commentRepository.save(comment).getId();
        }
        return commentId.toString();
    }

    public void deleteComment(String commentId) {
        log.info("Deleting A Comment With Id {}", commentId);
        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getAllComments(){
        log.info("Collecting All Comments");

        List<Comment> comments = commentRepository.findAll();

        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .build();
    }
}
