package kr.co.oauth.config.oauth;

import kr.co.oauth.user.entity.Role;
import kr.co.oauth.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
public class MemberProfile {

    private String name;
    private String email;
    private Set<Role> roles;
    private String provider;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    public User toMember() {
        return User.builder()
                .email(email)
                .name(name)
                .roles(roles)
                .provider(provider)
                .refreshToken(refreshToken)
                .build();
    }
}
