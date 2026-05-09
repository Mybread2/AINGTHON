package org.demo.aingthon.domain.auth.service;

import org.demo.aingthon.domain.auth.dto.OAuth2UserInfo;
import org.demo.aingthon.domain.auth.entity.Role;
import org.demo.aingthon.domain.auth.entity.User;
import org.demo.aingthon.domain.auth.repository.UserRepository;
import org.demo.aingthon.domain.auth.util.UniversityExtractor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        OAuth2UserInfo userInfo = OAuth2UserInfo.from(oAuth2User.getAttributes());

        String email = userInfo.email();
        if (!isUniversityEmail(email)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("not_university_email"),
                    "대학교 이메일(.ac.kr, .edu)로만 가입할 수 있습니다."
            );
        }

        String university = UniversityExtractor.extract(email);
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email, userInfo.name(), university, Role.USER)));

        return new DefaultOAuth2User(
                Collections.singleton(() -> user.getRole().name()),
                oAuth2User.getAttributes(),
                "email"
        );
    }

    private boolean isUniversityEmail(String email) {
        return email.endsWith(".ac.kr") || email.endsWith(".edu");
    }
}
