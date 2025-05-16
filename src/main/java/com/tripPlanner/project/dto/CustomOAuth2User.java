package com.tripPlanner.project.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// UserDTO에서 값을 받아 Provider쪽으로 유저정보를 넘겨주기 위해 담는 곳.

public class CustomOAuth2User implements OAuth2User {

    public final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO){
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Authorities 는 Role값 return

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDTO.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDTO.getName();

    }

    public String getUsername() {

        return userDTO.getUsername();
    }

}
