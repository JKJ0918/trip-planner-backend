package com.tripPlanner.project.service;

import com.tripPlanner.project.dto.JoinDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO){

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        String name = joinDTO.getName();
        String email = joinDTO.getEmail();

        boolean isExist = userRepository.existsByEmail(email);

        if(isExist) {
            // 이메일이 있을 경우
            return;
        }

        // 없을 경우

        UserEntity data = new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setName(name);
        data.setEmail(email);
        data.setRole("ROLE_USER");
        data.setSocialType("none");

        userRepository.save(data);
    }

}
