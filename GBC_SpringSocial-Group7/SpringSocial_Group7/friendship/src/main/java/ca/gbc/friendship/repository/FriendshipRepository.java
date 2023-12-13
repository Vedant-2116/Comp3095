package ca.gbc.friendship.repository;

import ca.gbc.friendship.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Define custom queries if necessary
    // For example, to find friendships by a specific user:
    List<Friendship> findByRequesterIdOrAddresseeId(Long requesterId, Long addresseeId);

    List<Friendship> findByUserId(Long userId);

}
