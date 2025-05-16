package com.tripPlanner.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    // 일반 로그인 JSON 요청 직접 파싱용
    private String username;
    private String password;
}
