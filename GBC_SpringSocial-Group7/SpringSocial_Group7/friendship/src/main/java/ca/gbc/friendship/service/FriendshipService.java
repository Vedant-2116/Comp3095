package ca.gbc.friendship.service;

import ca.gbc.friendship.model.Friendship;
import java.util.List;

public interface FriendshipService {

    /**
     * Sends a friendship request.
     *
     * @param requesterId the ID of the user sending the request.
     * @param addresseeId the ID of the user receiving the request.
     * @return the created Friendship entity.
     */
    Friendship sendRequest(Long requesterId, Long addresseeId);

    /**
     * Accepts a friendship request.
     *
     * @param requestId the ID of the friendship request.
     * @return the updated Friendship entity.
     */
    Friendship acceptRequest(Long requestId);

    /**
     * Declines a friendship request.
     *
     * @param requestId the ID of the friendship request.
     */
    void declineRequest(Long requestId);

    /**
     * Removes an existing friendship.
     *
     * @param friendshipId the ID of the friendship to be removed.
     */
    void removeFriendship(Long friendshipId);

    /**
     * Retrieves all friendships.
     *
     * @return a list of all Friendship entities.
     */
    List<Friendship> getAllFriendships();

    // Additional methods as needed
}
