package ca.gbc.friendship.service;

import ca.gbc.friendship.model.Friendship;
import ca.gbc.friendship.model.FriendshipStatus;
import ca.gbc.friendship.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final WebClient client;

    @Override
    public Friendship sendRequest(Long requesterId, Long addresseeId) {
        // Implementation of sending a friendship request
        // Example: Create a new Friendship entity and save it
        Friendship friendship = new Friendship();
        friendship.setRequesterId(requesterId);
        friendship.setAddresseeId(addresseeId);
        friendship.setStatus(FriendshipStatus.valueOf("REQUESTED")); // Assuming a status field
        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptRequest(Long requestId) {
        // Implementation of accepting a friendship request
        // Example: Update the status of the Friendship entity
        return friendshipRepository.findById(requestId)
                .map(friendship -> {
                    friendship.setStatus(FriendshipStatus.valueOf("ACCEPTED"));
                    return friendshipRepository.save(friendship);
                }).orElse(null); // Handle case where friendship is not found
    }

    @Override
    public void declineRequest(Long requestId) {
        // Implementation of declining a friendship request
        // Example: Update the status or delete the Friendship entity
        friendshipRepository.findById(requestId)
                .ifPresent(friendship -> {
                    friendship.setStatus(FriendshipStatus.valueOf("DECLINED"));
                    friendshipRepository.save(friendship);
                });
    }

    @Override
    public void removeFriendship(Long friendshipId) {
        // Implementation of removing a friendship
        friendshipRepository.deleteById(friendshipId);
    }

    @Override
    public List<Friendship> getAllFriendships() {
        // Return all friendships
        return friendshipRepository.findAll();
    }
}
