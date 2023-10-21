package ca.gbc.userservice.service;

import ca.gbc.userservice.dto.UserRequest;
import ca.gbc.userservice.dto.UserResponse;
import ca.gbc.userservice.model.User;
import ca.gbc.userservice.repository.UserRepository;
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
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public void createUser(UserRequest userRequest) {
        log.info("Creating A New User{}", userRequest.getUserName());
        User user = User.builder()
                .userName(userRequest.getUserName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .build();
        userRepository.save(user);
        log.info("User {} Is Saved", user.getId());
    }

    public String updateUser(String userId, UserRequest userRequest) {
        log.info("Updating A User With Id {}", userId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(userId));
        User user = mongoTemplate.findOne(query, User.class);

        if(user != null) {
            user.setUserName(userRequest.getUserName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());

            log.info("User {} is updated", user.getId());
            return userRepository.save(user).getId();
        }
        return userId.toString();
    }

    public void deleteUser(String userId) {
        log.info("Deleting A User With Id {}", userId);
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getAllUsers() {
        log.info("Collecting All Users");

        List<User> users = userRepository.findAll();

        return users.stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

}
