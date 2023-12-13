package ca.gbc.friendship.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "t_friendships")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "addressee_id")
    private Long addresseeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus status;


}
