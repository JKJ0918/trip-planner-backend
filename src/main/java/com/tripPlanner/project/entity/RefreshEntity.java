package com.tripPlanner.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class RefreshEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 어떤 유저에 대한 이름인지

    private String refresh; // 유저가 들고 있는 토큰

    private String expiration; // 토큰의 만료 시간

}
