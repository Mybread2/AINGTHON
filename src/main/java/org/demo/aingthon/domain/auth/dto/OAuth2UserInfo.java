package org.demo.aingthon.domain.auth.dto;

import java.util.Map;

public record OAuth2UserInfo(String email, String name) {

    public static OAuth2UserInfo from(Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                (String) attributes.get("email"),
                (String) attributes.get("name")
        );
    }
}