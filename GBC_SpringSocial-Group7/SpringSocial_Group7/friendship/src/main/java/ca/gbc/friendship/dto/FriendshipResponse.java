package ca.gbc.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private String status;

}