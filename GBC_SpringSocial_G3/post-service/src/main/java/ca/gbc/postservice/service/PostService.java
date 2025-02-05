package ca.gbc.postservice.service;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;

import java.util.List;

public interface PostService {
    void createPost(PostRequest postRequest);
    void deletePost(String postId);
    void updatePost(String postId, PostRequest postRequest);
    List<PostResponse> getAllPosts();

}
