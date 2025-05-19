package com.tripPlanner.project.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {
    // 회원가입 시 입력 받을 값
    private String username; // 아이디
    
    private String password; // 비밀번호
    
    private String nickname; // 닉네임

    private String name; // 이름
    
    private String email; // 이메일
    
    


}
