package ca.gbc.post.service;

import ca.gbc.post.dto.PostRequest;
import ca.gbc.post.dto.PostRespDt;
import ca.gbc.post.dto.PostResponse;

import java.util.List;

public interface PostService {
    String createPost(PostRequest postRequest);

    String updatePost(Long postId, PostRequest postRequest);

    void deletePost(Long postId);

    List<PostResponse> getAllPosts();

    List<PostResponse> getPostByUser(Long uId);

    PostRespDt getPost(Long postId);

    Boolean checkPost(Long postId);
}
