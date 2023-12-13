package ca.gbc.friendship.model;

public enum FriendshipStatus {
    // When a user has sent a friendship request
    REQUESTED,
    // When the request is accepted by the other user
    ACCEPTED,
    // When the request is declined
    DECLINED,
    // In case a user blocks another user
    BLOCKED
}
