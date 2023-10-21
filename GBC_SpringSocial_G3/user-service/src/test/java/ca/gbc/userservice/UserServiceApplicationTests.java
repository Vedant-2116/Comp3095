package ca.gbc.userservice;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MongoTemplate mongoTemplate;

    private UserRequest getUserRequest(){
        return UserRequest.builder()
                .userName("Vedantsinh Gohel")
                .email("101398199@georgebrown.ca")
                .password("211609")
                .build();
    }

    private List<User> getUserList(){
        List<User> userList = new ArrayList<>();
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id.toString())
                .userName("Vedantsinh Gohel")
                .email("101398199@georgebrown.ca")
                .password("211609")
                .build();

        userList.add(user);
        return userList;
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private List<UserResponse> convertJsonToObject(String jsonString)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, new TypeReference<List<UserResponse>>() {
        });
    }



    @Test
    void creatingUser() throws Exception {
        UserRequest userRequest = getUserRequest();
        String userRequestJson = convertObjectToJson(userRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .contentType("application/json")
                .content(userRequestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertTrue(userRepository.findAll().size() > 0);

        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userRequest.getUserName()));
        List<User> userList = mongoTemplate.find(query, User.class);
        assertEquals(userList.get(0).getUserName(), userRequest.getUserName());
    }

    @AfterEach
    void cleanUp(){
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() throws Exception {
        userRepository.saveAll(getUserList());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user")
                .accept(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andDo(MockMvcResultHandlers.print());

        MvcResult result = response.andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(jsonResponse);

        int actualSize = jsonNode.size();
        int expectedSize = getUserList().size();

        assertEquals(expectedSize, actualSize);
    }

    @Test
    void updateUser() throws Exception{
        User saveUser = User.builder()
                .id(UUID.randomUUID().toString())
                .userName("Vedantsinh Gohel")
                .email("101398199@georgebrown.ca")
                .password("211609")
                .build();

        userRepository.save(saveUser);

        saveUser.setEmail("101398199@georgebrown.ca");
        String userRequstString = objectMapper.writeValueAsString(saveUser);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/user/" + saveUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequstString));

        response.andExpectAll(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(saveUser.getId().toString()));
        User storeduser = mongoTemplate.findOne(query, User.class);

        assertEquals(saveUser.getEmail(), storeduser.getEmail());
    }

    @Test
    void deleteUser() throws Exception {
        User saveUser = User.builder()
                .id(UUID.randomUUID().toString())
                .userName("Vedantsinh Gohel")
                .email("101398199@georgebrown.ca")
                .password("211609")
                .build();

        userRepository.save(saveUser);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/user/" + saveUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
        response.andDo(MockMvcResultHandlers.print());

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(saveUser.getId().toString()));
        Long storeduser = mongoTemplate.count(query, User.class);

        assertEquals(0, storeduser);
    }

}
