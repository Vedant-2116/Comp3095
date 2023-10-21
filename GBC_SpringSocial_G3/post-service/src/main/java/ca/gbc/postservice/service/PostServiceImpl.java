package ca.gbc.postservice.service;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
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
public class PostServiceImpl {
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    public void createPost(PostRequest postRequest) {
        log.info("Creating A New Post{}", postRequest.getContent());
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .build();
        postRepository.save(post);
        log.info("Post {} Is Saved", post.getId());
    }

    public String updatePost(String postId,PostRequest postRequest) {
        log.info("Updating A Post With Id {}", postId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(postId));
        Post post = mongoTemplate.findOne(query, Post.class);

        if(post != null) {
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            log.info("Post {} Is Updated", post.getId());
            return postRepository.save(post).getId();
        }
        return postId.toString();

    }

    public void deletePost(String postId) {
        log.info("Deleting A Post With Id {}", postId);
        postRepository.deleteById(postId);
    }

    public List<PostResponse> getAllPosts() {
        log.info("Collecting All Posts");

        List<Post> posts = postRepository.findAll();

        return posts.stream().map(this::mapToPostResponse).collect(Collectors.toList());
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

}
