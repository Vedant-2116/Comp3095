package ca.gbc.friendship.controller;

import ca.gbc.friendship.model.Friendship;
import ca.gbc.friendship.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendship")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PostMapping("/request")
    public ResponseEntity<Friendship> sendFriendshipRequest(@RequestParam Long requesterId, @RequestParam Long addresseeId) {
        Friendship friendship = friendshipService.sendRequest(requesterId, addresseeId);
        return ResponseEntity.ok(friendship);
    }

    @PostMapping("/accept")
    public ResponseEntity<Friendship> acceptFriendship(@RequestParam Long requestId) {
        Friendship updatedFriendship = friendshipService.acceptRequest(requestId);
        return ResponseEntity.ok(updatedFriendship);
    }

    @PostMapping("/decline")
    public ResponseEntity<Friendship> declineFriendship(@RequestParam Long requestId) {
        friendshipService.declineRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriendship(@RequestParam Long friendshipId) {
        friendshipService.removeFriendship(friendshipId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Friendship>> getAllFriendships() {
        List<Friendship> friendships = friendshipService.getAllFriendships();
        return ResponseEntity.ok(friendships);
    }


}
