package kr.co.oauth.member.dto.responseDto;

import kr.co.oauth.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {

  private Long id;
  private String nickname;
  private String myImgUrl;
  private String accessToken;
  private String refreshToken;



  public LoginResponseDto(Member member, String accessToken, String refreshToken) {
    this.id = member.getId();
    this.nickname = member.getNickname();
    this.myImgUrl = member.getMyImgUrl();
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

}

