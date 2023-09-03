package kr.co.oauth.member.entity;

import kr.co.oauth.common.entity.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "Member3")
public class Member extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name= "email", nullable=false)
  private String email;
  @Column(nullable = false)
  private String password;
  private String name;
  private String nickname;
  private String myImgUrl;
  private String provider;

  private Boolean isDeleted; // 탈퇴 여부
  private String refreshToken;

  @Builder
  public Member(String email, String password,String name, String nickname, String myImgUrl, String provider, Boolean isDeleted, String refreshToken) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.myImgUrl = myImgUrl;
    this.provider = provider;
    this.isDeleted = isDeleted;
    this.refreshToken = refreshToken;
  }

  public Member update(String name, String email, String refreshToken) {
    this.name = name;
    this.email = email;
    this.refreshToken = refreshToken;
    return this;
  }

  public Member updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
    return this;
  }
}
