package com.tripPlanner.project.repository;

import com.tripPlanner.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    Optional<UserEntity> findByEmailAndSocialType(String email, String socialType); // 소셜로그인 가입여부 확인
    
    Boolean existsByEmail(String email); // 가입 여부 이메일 확인

    UserEntity findByUsername(String username);

    UserEntity findByNameAndSocialType(String username, String socialType); // 소셜 로그인 추가 정보 기입

    UserEntity findByNickname(String nickname);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);

    UserEntity findByName(String name);

}
