package com.tripPlanner.project.dto.ws;

public class TokenUserInfo {
    private final String username;
    private final String socialType;
    public TokenUserInfo(String username, String socialType) {
        this.username = username; this.socialType = socialType;
    }
    public String getUsername() { return username; }
    public String getSocialType() { return socialType; }
}
