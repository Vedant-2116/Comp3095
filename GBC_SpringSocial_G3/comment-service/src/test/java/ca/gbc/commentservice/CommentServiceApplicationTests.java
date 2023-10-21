package ca.gbc.commentservice;

import ca.gbc.commentservice.dto.CommentRequest;
import ca.gbc.commentservice.dto.CommentResponse;
import ca.gbc.commentservice.model.Comment;
import ca.gbc.commentservice.repository.CommentRepository;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class CommentServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    private CommentRequest getCommentRequest(){
        return CommentRequest.builder()
                .content(" A Comment")
                .build();
    }

    private List<Comment> getCommentList(){
        List<Comment> commentList = new ArrayList<>();
        UUID uuid = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(uuid.toString())
                .content("This Is A Comment")
                .build();

        commentList.add(comment);
        return commentList;
    }
    private String convertObjectToJson(List<CommentResponse> commentList)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(commentList);
    }

    private List<CommentResponse> convertJsonToObject(String json)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<List<CommentResponse>>() {});
    }

    @Test
    void createComment() throws Exception{
        CommentRequest commentRequest = getCommentRequest();
        String commentRequestString = objectMapper.writeValueAsString(commentRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType("application/json")
                .content(commentRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(commentRepository.findAll().size() > 0);

        Query query = new Query();
        query.addCriteria(Criteria.where("content").is("This Is A Comment"));
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        Assertions.assertTrue(commentList.size() > 0);
    }

    @AfterEach
    void cleanup(){
        commentRepository.deleteAll();
    }

    @Test
    void getAllComments() throws Exception{

        commentRepository.saveAll(getCommentList());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/comment")
                .accept(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonRespone = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonRespone);

        int actualSize = jsonNode.size();
        int expectedSize = getCommentList().size();

        Assertions.assertEquals(expectedSize, actualSize);
    }

    @Test
    void updateComments() throws Exception{

        Comment savedComment = commentRepository.save(Comment.builder()
                .content("This Is A Comment")
                .build());

        commentRepository.save(savedComment);

        savedComment.setContent("This Is A Comment");
        String commentRequestString = objectMapper.writeValueAsString(savedComment);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/comment/" + savedComment.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestString));

        response.andExpectAll(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedComment.getId()));
        Comment storedComment = mongoTemplate.findOne(query, Comment.class);

        Assertions.assertTrue(savedComment.getContent().equals(storedComment.getContent()));

    }

    @Test
    void deleteComment() throws Exception{
        Comment savedComment = commentRepository.save(Comment.builder()
                .content("This Is A Comment")
                .build());

        commentRepository.save(savedComment);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/comment/" + savedComment.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(savedComment.getId()));
        Long commentCount = mongoTemplate.count(query, Comment.class);

        Assertions.assertEquals(0, commentCount);
    }
}
