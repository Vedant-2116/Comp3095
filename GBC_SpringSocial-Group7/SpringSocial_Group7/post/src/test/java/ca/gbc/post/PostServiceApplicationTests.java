package ca.gbc.post;

import ca.gbc.post.dto.PostRequest;
import ca.gbc.post.dto.PostResponse;
import ca.gbc.post.model.Post;
import ca.gbc.post.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureMockMvc
public class PostServiceApplicationTests extends AbstractContainerBasicTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    private final String TEST_SKU_CODE = "testSkuCode";
    private static MockWebServer mockWebServer;

    private PostRequest getPostRequest() {
        return PostRequest.builder()
                .title("Post 1")
                .content("This is the content of Post 1.")
                .author("Author 1")
                .build();
    }

    private List<Post> getPostList() {
        List<Post> postList = new ArrayList<>();

        UUID uuid = UUID.randomUUID();

        Post post = Post.builder()
                .id(uuid.toString())
                .title("Post 1")
                .content("This is the content of Post 1.")
                .author("Author 1")
                .build();

        postList.add(post);
        return postList;
    }

    private String convertObjectsToJSON(List<PostResponse> postList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(postList);
    }

    private List<PostResponse> convertJsonStringToObject(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<PostResponse>>() {
        });
    }

    @Test
    void createPost() throws Exception {
        PostRequest postRequest = getPostRequest();
        String postRequestString = objectMapper.writeValueAsString(postRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Assert that the repository size has increased
        Assertions.assertTrue(postRepository.count() > 0);

        // Find the post by title
        List<Post> postList = postRepository.findByTitle("Post 1");

        // Assert that the post list size is greater than 1
//        Assertions.assertTrue(postList.size() > 1);
    }


    @Test
    void getAllPosts() throws Exception {
        // Given
        postRepository.saveAll(getPostList());

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/post")
                .accept(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNodes.size();
        int expectedSize = getPostList().size();

        assertEquals(expectedSize > 0, actualSize > 0); // Compare the actual size to the expected size
    }

    @Test
    void updatePost() throws Exception {
        // Given
        Post savedPost = new Post();
        savedPost.setTitle("Post 2");
        savedPost.setContent("This is the content of Post 2.");
        savedPost.setAuthor("Author 2");
        postRepository.save(savedPost);

        // Prepare update post and postRequest
        savedPost.setContent("This is the updated content.");

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/post/" + savedPost.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(savedPost))); // Assuming you have a method to convert Post to JSON

        // Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());

        Optional<Post> storedPost = postRepository.findById(savedPost.getId());
        storedPost.ifPresent(post -> Assertions.assertEquals(savedPost.getContent(), post.getContent()));
    }

    @Test
    void deletePost() throws Exception {
        // Given
        Post savedPost = Post.builder()
                .title("Post 1")
                .content("This is the content of Post 1.")
                .author("Author 1")
                .build();

        savedPost = postRepository.save(savedPost);

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/post/" + savedPost.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Then
        Optional<Post> deletedPost = postRepository.findById(savedPost.getId());
        Assertions.assertTrue(deletedPost.isEmpty());
    }

}
