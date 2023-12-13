package ca.gbc.post.service;

import ca.gbc.post.dto.*;
import ca.gbc.post.model.Post;
import ca.gbc.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final WebClient client;

    @Value("${comment.service.url}")
    private String commentApiUri;

    @Value("${user.service.url}")
    private String userApiUrl;


    @Override
    public String createPost(PostRequest postRequest) {

        log.info("Creating a new post: {}", postRequest.getTitle());

        Post post = new Post(); // Assuming you have setters or a no-args constructor
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setAuthor(postRequest.getAuthor());

        postRepository.save(post);
        log.info("Post {} is saved", post.getId());

        return "Post Created Successfully";

    }

    @Override
    public String updatePost(Long postId, PostRequest postRequest) {
        return String.valueOf(postRepository.findById(postId)
                .map(post -> {
                    post.setTitle(postRequest.getTitle());
                    post.setContent(postRequest.getContent());
                    postRepository.save(post);
                    log.info("Comment {} is updated", post.getId());
                    return post.getId();
                })
                .orElse(postId));
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
        log.info("Deleting comment with Id: {}", postId);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(x -> {
                    List<CommentResponse> comments = getComment(x.getId());
                    return mapToPostResponse(x, comments);
                }).toList();
    }

    @Override
    public List<PostResponse> getPostByUser(Long userId) {
        List<Post> posts = postRepository.findByAuthor(userId);
        return posts.stream().map(post -> {
            List<CommentResponse> comments = getComment(post.getId());
            return mapToPostResponse(post, comments);
        }).collect(Collectors.toList());
    }


    @Override
    public PostRespDt getPost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<CommentResponse> comments = getComment(postId);
            return mapToFullPostResponse(post, comments);
        } else {
            // Handle the case where the post is not found.
            // This could be returning null, throwing an exception, or returning a default PostRespDt
            return null; // or handle differently
        }
    }

    private PostResponse mapToPostResponse(Post post, List<CommentResponse> comments) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(getUser(post.getAuthor()))
                .comment(comments)
                .build();
    }

    private PostRespDt mapToFullPostResponse(Post post, List<CommentResponse> comments) {
        return PostRespDt.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .author(getUser(post.getAuthor()))
                .comment(comments)
                .build();
    }
    private List<CommentResponse> getComment(Long postId){
        log.info("Fetching comments for post: {}", postId);

        return client
                .get()
                .uri(commentApiUri + "/" + postId)
                .retrieve()
                .bodyToFlux(CommentResponse.class)
                .collectList()
                .block();
    }

    private UserResponse getUser(Long uId){
        List<UserResponse> client.get()
                .uri(userApiUrl + "/" + uId)
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .collectList()
                .block();
    }
    private UserResponse getUser(Long userId) {
        List<UserResponse> users = client.get()
                .uri(userApiUrl + "/" + userId)
                .retrieve()
                .bodyToFlux(UserResponse.class)
                .collectList()
                .block();

        // Assuming the list contains only one user
        return users.isEmpty() ? null : users.get(0);
    }

    public Boolean checkPost(Long postId){
        Optional<Post> post = postRepository.findById(postId);
        return post.isPresent();

    }


}
