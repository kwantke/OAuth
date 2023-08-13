package kr.co.oauth.config.oauth;

import kr.co.oauth.domain.Member2;
import kr.co.oauth.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfile {

    private String name;
    private String email;
    private String provider;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    public Member toMember() {
        return Member.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .refreshToken(refreshToken)
                .build();
    }
}
