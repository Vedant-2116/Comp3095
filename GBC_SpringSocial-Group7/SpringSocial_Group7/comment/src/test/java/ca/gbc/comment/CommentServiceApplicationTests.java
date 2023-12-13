package ca.gbc.comment;

import ca.gbc.comment.dto.CommentRequest;
import ca.gbc.comment.dto.CommentResponse;
import ca.gbc.comment.model.Comment;
import ca.gbc.comment.repository.CommentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureMockMvc
public class CommentServiceApplicationTests extends AbstractContainerBaseTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;


    @Autowired
    private ObjectMapper objectMapper;

    private CommentRequest getCommentRequest() {
        return CommentRequest.builder()
                .postId("1")
                .content("This is a comment.")
                .author("Commenter")
                .build();
    }

    private List<Comment> getCommentList() {
        List<Comment> commentList = new ArrayList<>();

        UUID uuid = UUID.randomUUID();

        Comment comment = Comment.builder()
                .id(uuid.toString())
                .postId("1")
                .content("This is a comment.")
                .author("Commenter")
                .build();

        commentList.add(comment);
        return commentList;
    }

    private String convertObjectsToJSON(List<CommentResponse> commentList) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(commentList);
    }

    private List<CommentResponse> convertJsonStringToObject(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<CommentResponse>>() {
        });
    }

    @Test
    void createComment() throws Exception {
        CommentRequest comment = getCommentRequest();
        String commentRequestString = objectMapper.writeValueAsString(comment);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestString))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(commentRepository.count() < 0);

        // Use JPA repository method to find the comment
        List<Comment> commentList = commentRepository.findByContent("This is a comment.");
        Assertions.assertTrue(commentList.size() < 0);
    }

    @Test
    void getAllComments() throws Exception {
        // Given
        commentRepository.saveAll(getCommentList());

        // When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/comment")
                .accept(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNodes.size();
        int expectedSize = getCommentList().size();

        assertEquals(expectedSize > 0, actualSize > 0); // Compare the actual size to the expected size
    }

    @Test
    void updateComment() throws Exception {
        // Given
        Comment savedComment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .postId("1")
                .content("This is an old comment.")
                .author("Old Commenter")
                .build();

        // Save the original comment
        commentRepository.save(savedComment);

        // Prepare the updated comment
        savedComment.setContent("This is the updated comment.");
        String commentRequestString = objectMapper.writeValueAsString(savedComment);

        // When: Perform the update operation via MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/comment/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestString))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Then: Fetch the updated comment from the database and assert
        Comment updatedComment = commentRepository.findById(savedComment.getId()).orElse(null);

        Assertions.assertNotNull(updatedComment);
        Assertions.assertEquals("This is the updated comment.", updatedComment.getContent());
    }


    @Test
    void deleteComment() throws Exception {
        // Given
        Comment savedComment = Comment.builder()
                .id(UUID.randomUUID().toString())
                .postId("1")
                .content("This is a comment.")
                .author("Commenter")
                .build();

        commentRepository.save(savedComment);

        // When: Perform the delete operation via MockMvc
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/comment/" + savedComment.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Then: Check if the comment is deleted
        Comment deletedComment = commentRepository.findById(savedComment.getId()).orElse(null);

        Assertions.assertNull(deletedComment); // The comment should be deleted
    }

}

