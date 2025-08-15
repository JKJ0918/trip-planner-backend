package com.tripPlanner.project.dto.myPage;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MeDTO {
    // DTO
    public record MeResponse(Long id, String nickname, String email) {}

    public record UpdateMeRequest(String nickname) {}

}
