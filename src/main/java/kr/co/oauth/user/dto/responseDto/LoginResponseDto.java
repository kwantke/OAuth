package kr.co.oauth.user.dto.responseDto;

import kr.co.oauth.user.entity.Role;
import kr.co.oauth.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

  private Long id;
  private String nickname;
  private String myImgUrl;
  private Set<Role> roles;
  private String accessToken;
  private String refreshToken;

  public LoginResponseDto(User user, String accessToken, String refreshToken) {
    this.id = user.getId();
    this.nickname = user.getNickname();
    this.myImgUrl = user.getMyImgUrl();
    this.roles = user.getRoles();
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

}

