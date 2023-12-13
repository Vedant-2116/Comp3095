package ca.gbc.user.controller;

import ca.gbc.user.dto.UserRequest;
import ca.gbc.user.dto.UserResponse;
import ca.gbc.user.service.UserService;
import ca.gbc.user.service.UserServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "userService", fallbackMethod = "userService")
    @TimeLimiter(name = "userService")
    @Retry(name = "userService")
    public CompletableFuture<ResponseEntity<String>> createUser(@RequestBody UserRequest userRequest) {

        return CompletableFuture.supplyAsync(() -> {
            userService.createUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User Created Successfully");
        });
    }

    public CompletableFuture<ResponseEntity<String>> placeUserFallback(UserRequest request, Throwable e) {
        log.error("Exception is: {}", e.getMessage());
        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("FALLBACK INVOKED: User Failed. Please try again later."));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        String updatedUserId = userService.updateUser(id, userRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/users/" + updatedUserId);

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
