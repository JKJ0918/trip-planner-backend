package com.tripPlanner.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDTO {
    private String socialType;

    public UserInfoResponseDTO(String socialType) {
        this.socialType = socialType;
    }
}
