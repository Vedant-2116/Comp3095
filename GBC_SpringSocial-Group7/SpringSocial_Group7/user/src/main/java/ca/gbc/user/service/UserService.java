package ca.gbc.user.service;

import ca.gbc.user.dto.UserRequest;
import ca.gbc.user.dto.UserResponse;

import java.util.List;

public interface UserService {
    void createUser(UserRequest userRequest);

    String updateUser(Long userId, UserRequest userRequest);

    void deleteUser(Long userId);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);



}
