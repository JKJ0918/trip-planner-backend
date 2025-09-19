package com.tripPlanner.project.dto.ws;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WsUserPrincipal {
    private final Long userId;
    private final String username;
    private final String socialType;
    public WsUserPrincipal(Long userId, String username, String socialType) {
        this.userId = userId;
        this.username = username;
        this.socialType = socialType;
    }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getSocialType() { return socialType; }
}
