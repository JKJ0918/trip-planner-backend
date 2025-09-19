package com.tripPlanner.project.dto;

import com.tripPlanner.project.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails (UserEntity userEntity){
        this.userEntity = userEntity;
    }

    // ✅ 여기 추가: 서비스 코드에서 userId/socialType을 바로 꺼낼 수 있도록
    public Long getId() {
        return userEntity.getId();
    }

    // 필요 시 사용 (토큰/로깅/검증 등)
    public String getSocialType() {
        // socialType 타입에 맞게 수정 (enum이면 .name(), 문자열이면 그대로)
        // 예: return userEntity.getSocialType().name();
        return userEntity.getSocialType();
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {

                return userEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
