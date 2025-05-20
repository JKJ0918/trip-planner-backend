package com.tripPlanner.project.service;

import com.tripPlanner.project.dto.*;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {


    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    // OAuth2UserRequest : User Information from Resource Server
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("리소스 서버로 부터 받아온 유저정보 확인 : " + oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("OAuth2 registrationId: " + registrationId); // ✅ 이거 꼭 찍어보세요 20250424
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Optional<UserEntity> userOpt = userRepository.findByEmailAndSocialType(oAuth2Response.getEmail(), registrationId);

        // 신규 가입
        if (userOpt.isEmpty()) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole("ROLE_USER_A");
            userEntity.setSocialType(registrationId); // 소셜 로그인 타입

            userRepository.save(userEntity);

            // OAuth2User에 유저 정보 담기
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER_A");
            userDTO.setSocialType(registrationId);
            System.out.println("OAuth2 registrationId 값 넣기전 : " + registrationId);
            System.out.println("OAuth2 registrationId 값 넣기전2 userDTO.setSocialType(registrationId); : " + userDTO.getSocialType());
            return new CustomOAuth2User(userDTO);
        }
        else { // 기존 회원

            UserEntity existData = userOpt.get();  // 실제 UserEntity 꺼내기

            existData.setEmail(oAuth2Response.getEmail());
            existData.setName(oAuth2Response.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRole());
            userDTO.setSocialType(registrationId);

            return new CustomOAuth2User(userDTO);
        }

    }

}
