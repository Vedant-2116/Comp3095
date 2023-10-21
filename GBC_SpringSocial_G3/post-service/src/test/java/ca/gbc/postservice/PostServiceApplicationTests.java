package ca.gbc.postservice;

import ca.gbc.postservice.dto.PostRequest;
import ca.gbc.postservice.dto.PostResponse;
import ca.gbc.postservice.model.Post;
import ca.gbc.postservice.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class PostServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    private PostRequest getPostRequest(){
        return PostRequest.builder()
                .title("Apple iPad 2024")
                .content("Apple iPad version 2024")
                .build();
    }

    private List<Post> getPostList(){
        List<Post> postList = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        Post post = Post.builder()
                .id(uuid.toString())
                .title("Apple iPad 2024")
                .content("Apple iPad version 2024")
                .build();

        postList.add(post);
        return postList;
    }

    private String convertObjectToJson(List<PostResponse> postList)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(postList);
    }

    private List<PostResponse> convertJsonToObject(String json)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<PostResponse>>() {
        });
    }

    @Test
    void creatingUser() throws Exception{
        PostRequest postRequest = getPostRequest();
        String postRequestString = objectMapper.writeValueAsString(postRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType("application/json")
                        .content(postRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(postRepository.findAll().size() > 0);

        Query query = new Query();
        query.addCriteria(Criteria.where("title").is("Apple iPad 2024"));
        List<Post> postList = mongoTemplate.find(query, Post.class);
        Assertions.assertTrue(postList.size() > 0);
    }

    @AfterEach
    void cleanup(){
        postRepository.deleteAll();
    }

    @Test
    void getAllProducts() throws Exception {

        postRepository.saveAll(getPostList());

        ResultActions respones = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/post")
                .accept(MediaType.APPLICATION_JSON));

        respones.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MvcResult result = respones.andReturn();
        String jsonRespone = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonRespone);

        int actualSize = jsonNode.size();
        int expectedSize = postRepository.findAll().size();
        assertEquals(expectedSize, actualSize);
    }

    @Test
    void updatePost() throws Exception{

        Post savedPost = postRepository.save(Post.builder()
                .title("Apple Macbook M3 ")
                .content("Apple Macbook Version 3")
                .build());

        postRepository.save(savedPost);

        savedPost.setTitle("Apple Macbook M4");
        String postRequestString = objectMapper.writeValueAsString(savedPost);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.
                put("/api/post/" + savedPost.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestString));

        response.andExpectAll(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedPost.getId()));
        Post storedPost = mongoTemplate.findOne(query, Post.class);

        Assertions.assertEquals(savedPost.getTitle(), storedPost.getTitle());
    }

    @Test
    void deletePost() throws Exception{
        Post savedPost = postRepository.save(Post.builder()
                .title("Apple Macbook M3 ")
                .content("Apple Macbook Version 3")
                .build());

        postRepository.save(savedPost);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/post/" + savedPost.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedPost.getId()));
        Long postCount = mongoTemplate.count(query, Post.class);

        Assertions.assertEquals(0, postCount);

    }
}
