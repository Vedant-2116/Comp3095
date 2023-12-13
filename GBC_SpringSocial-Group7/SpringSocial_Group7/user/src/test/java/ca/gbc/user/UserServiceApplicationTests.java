package ca.gbc.user;

import ca.gbc.user.dto.UserRequest;
import ca.gbc.user.dto.UserResponse;
import ca.gbc.user.model.User;
import ca.gbc.user.repository.UserRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureMockMvc
public class UserServiceApplicationTests extends AbstractContainerBasicTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final String TEST_SKU_CODE = "testSkuCode";
//    private static MockWebServer mockWebServer;
//
//    private UserRequest getUserRequest() {
//        return UserRequest.builder()
//                .username("user 1")
//                .email("user1@gmail.com")
//                .password("user123")
//                .build();
//    }
//
//    private List<User> getUserList() {
//        List<User> userList = new ArrayList<>();
//        User user = User.builder()
//                .id(UUID.randomUUID().toString())
//                .username("user 1")
//                .email("user1@gmail.com")
//                .password("user123")
//                .build();
//        userList.add(user);
//        return userList;
//    }
//
//    private String convertObjectsToJSON(List<UserResponse> productList) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.writeValueAsString(productList);
//    }
//
//    private List<UserResponse> convertJsonStringToObject(String jsonString) throws JsonProcessingException{
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.readValue(jsonString, new TypeReference<List<UserResponse>>() {
//        });
//    }
//    @Test
//    void createUser() throws Exception {
//        UserRequest userRequest = getUserRequest();
//        String json = objectMapper.writeValueAsString(userRequest);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//
//        Assertions.assertTrue(userRepository.count() > 0);
//    }
//    @Test
//    void getAllUser() throws Exception {
//        userRepository.saveAll(getUserList());
//
//        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/users")  // Updated the URL
//                .accept(MediaType.APPLICATION_JSON));
//
//        response.andExpect(MockMvcResultMatchers.status().isOk());
//        response.andDo(MockMvcResultHandlers.print());
//
//        MvcResult result = response.andReturn();
//        String jsonResponse = result.getResponse().getContentAsString();
//        JsonNode jsonNodes = new ObjectMapper().readTree(jsonResponse);
//
//        assertEquals(!getUserList().isEmpty(), jsonNodes.size() > 0);
//    }
//
//    @Test
//    void updateUser() throws Exception {
//        // Create a user to update
//        User user = userRepository.save(new User("user2", "user2@gmail.com", "123user"));
//
//        // Update user details
//        user.setPassword("newPassword");
//        String json = objectMapper.writeValueAsString(user);
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/" + user.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//
//        User updatedUser = userRepository.findById(user.getId()).orElse(null);
//        Assertions.assertNotNull(updatedUser);
//        Assertions.assertEquals("newPassword", updatedUser.getPassword());
//    }
//
//    @Test
//    void deleteUser() throws Exception {
//        // Create a user to delete
//        User user = userRepository.save(new User("user3", "user3@gmail.com", "user123"));
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + user.getId())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isNoContent());
//
//        boolean userExists = userRepository.existsById(user.getId());
//        Assertions.assertFalse(userExists);
//    }
}
