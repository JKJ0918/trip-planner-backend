package com.tripPlanner.project.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

/*  { 네이버 데이터 JSON
        resultcode=00, message=success, response={id=123123123, name=개발자}
    }*/
    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
