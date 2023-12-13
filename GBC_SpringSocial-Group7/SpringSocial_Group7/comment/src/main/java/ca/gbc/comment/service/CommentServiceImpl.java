package ca.gbc.comment.service;

import ca.gbc.comment.dto.CommentRequest;
import ca.gbc.comment.dto.CommentResponse;
import ca.gbc.comment.dto.PostResponse;
import ca.gbc.comment.dto.UserResponse;
import ca.gbc.comment.model.Comment;
import ca.gbc.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final WebClient client;

    @Value("${post.service.url}")
    private String postApiUrl;

    @Value("${user.service.url}")
    private String userApiUrl;

    @Override
    public void createComment(CommentRequest commentRequest) {
        if (!checkPost(commentRequest.getPostId())) return;

        UserResponse user = client.get()
                .uri(userApiUrl + "/" + commentRequest.getAuthor())
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .blockFirst();

        if (user == null) {
            log.info("The user does not exist.");
            return;
        }


        log.info("Making a new comment: {}", commentRequest.getContent());
        Comment comment = new Comment();
        comment.setPostId(commentRequest.getPostId());
        comment.setContent(commentRequest.getContent());
        comment.setAuthor(commentRequest.getAuthor());

        try {
            commentRepository.save(comment);
            log.info("Comment {} is saved", comment.getId());
        }catch (Exception e){
            log.error("error : {} ", e.getMessage());
        }
    }

    @Override
    public String updateComment(Long commentId, CommentRequest commentRequest) {
        if (!checkPost(commentRequest.getPostId())) return "Post Does not exist";

        UserResponse user = client.get()
                .uri(userApiUrl + "/" + commentRequest.getAuthor())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();

        if (user == null) {
            log.info("The user does not exist.");
            return "The user does not exist.";
        }


        return commentRepository.findById(commentId)
                .map(comment -> {
                    comment.setContent(commentRequest.getContent());
                    commentRepository.save(comment);
                    log.info("Comment {} is updated", comment.getId());
                    return comment.getId().toString();
                })
                .orElse(commentId.toString());
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Deleting comment with Id: {}", commentId);
    }

    @Override
    public List<CommentResponse> getCommentsForPost(Long postId) {
        if (!checkPost(postId) ) return null;
        List<Comment> comments = commentRepository.findCommentsByPostId(postId);
        return comments.stream().map(this::mapCommentResp).toList();
    }

//    @Override
//    public List<CommentResponse> getAllComments() {
//        List<Comment> comments = commentRepository.findAll();
//        return comments.stream()
//                .map(comment -> new CommentResponse(
//                        comment.getId(),
//                        comment.getPostId(),
//                        comment.getContent(),
//                        comment.getAuthor()))
//                .collect(Collectors.toList());
//    }

    private CommentResponse mapCommentResp(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(getUser(comment.getAuthor()))
                .build();
    }
    private UserResponse getUser(Long author) {
        return client
                .get()
                .uri(userApiUrl + "/{id}", author)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
    }

    private Boolean checkPost(Long postId){
        return client.get()
                .uri(postApiUrl + "/check/" + postId)
                .retrieve()
                .bodyToFlux(Boolean.class)
                .blockFirst();
    }



}