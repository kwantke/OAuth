package kr.co.oauth.config.oauth;

import kr.co.oauth.domain.Member2;
import kr.co.oauth.member.entity.Member;
import kr.co.oauth.member.repository.MemberRepository;
import kr.co.oauth.repository.MemberRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/*
    OAuth2 로그인 성공시 DB에 저장하는 작업
* */
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    //loadUser 메서드 설명
    // Spring Security가 access token을 이용해서 OAuth2 Service에서 유저 정보를 가져온 다음 loadUser 메서드를 통해 유저 정보를 가져옴
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        //OAuth 서비스 (kakao, google, naver)에서 가져온 유저 정보를 담고 있음
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth 서비스 이름 (kakao, google, naver)
        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        //Oauth 로그인 시 키(pk)가 되는 값
        //Oauth 서비스의 유저 정보들
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌
        MemberProfile memberProfile = OAuthAttributes.extract(registrationId, attributes);
        memberProfile.setProvider(registrationId);
        Member member = saveOrUpdate(memberProfile);

        Map<String, Object> customAttribute = customAttribute(attributes, userNameAttributeName, memberProfile, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")), customAttribute, userNameAttributeName);

    }

    private Map customAttribute(Map attributes, String userNameAttributeName, MemberProfile memberProfile, String registrationId) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName,attributes.get(userNameAttributeName));
        customAttribute.put("provider", registrationId);
        customAttribute.put("name", memberProfile.getName());
        customAttribute.put("email", memberProfile.getEmail());
        return customAttribute;
    }

    private Member saveOrUpdate(MemberProfile memberProfile) {
        Member member = memberRepository.findByEmailAndProvider(memberProfile.getEmail(),
                memberProfile.getProvider())
                .map(m-> m.update(memberProfile.getName(), memberProfile.getEmail()))
                .orElse(memberProfile.toMember());
        return memberRepository.save(member);
    }
}
