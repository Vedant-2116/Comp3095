package ca.gbc.post.controller;

import ca.gbc.post.dto.PostRequest;
import ca.gbc.post.dto.PostRespDt;
import ca.gbc.post.dto.PostResponse;
import ca.gbc.post.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostRequest postRequest) {

        postService.createPost(postRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPosts() {

        return postService.getAllPosts();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId, @RequestBody PostRequest postRequest) {
        String updatePostId = postService.updatePost(postId, postRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/post/" + updatePostId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves a list of all posts by a user.
     *
     * @param userId The ID of the user to retrieve posts for.
     * @return A list of PostResponse objects representing all the posts.
     */
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPostsByUser(@PathVariable("userId") Long userId) {
        return postService.getPostByUser(userId);
    }

    /***
     * Retrieves a post by post ID.
     * @param postId The ID of the post to retrieve.
     * @return A PostResponseFullData object representing the post.
     */
    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostRespDt getPost(@PathVariable("postId") Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/check/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkPost(@PathVariable("postId") Long postId) { return postService.checkPost(postId); }
}
