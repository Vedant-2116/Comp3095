package ca.gbc.user.service;

import ca.gbc.user.dto.UserRequest;
import ca.gbc.user.dto.UserResponse;
import ca.gbc.user.model.User;
import ca.gbc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WebClient client;

    @Value("${friendship.service.url}")
    private String friendshipURL;


    @Override
    public void createUser(UserRequest userRequest) {
        log.info("Creating a new user {}", userRequest.getUsername());
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        userRepository.save(user);
        log.info("User {} is saved", user.getId());
    }

//    @Override
//    public String updateUser(Long userId, UserRequest userRequest) {
//        return Long.valueOf(userRepository.findById(userId)
//                .map(user -> {
//                    user.setUsername(userRequest.getUsername());
//                    user.setEmail(userRequest.getEmail());
//                    user.setPassword(userRequest.getPassword());
//                    userRepository.save(user);
//                    log.info("User {} is updated", user.getId());
//                    return user.getId();
//                }).orElse(userId));
//    }

    @Override
    public String updateUser(Long userId, UserRequest userRequest) {
        log.info("Updating user with id: {}", userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setEmail(userRequest.getEmail());
            user.setUsername(userRequest.getUsername());
            user.setPassword(userRequest.getPassword());
            log.info("User {} is updated", user.getId());
            userRepository.save(user);
        });

        return "User has been updated";
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User {} is deleted", userId);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("Returning a list of users");
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(toList());
    }

    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user with Id {}", userId);
        return userRepository.findById(userId)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .orElse(null); // or throw a custom exception
    }

}
