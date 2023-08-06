package kr.co.oauth.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "Member3")
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;

  private String name;
  private String nickname;
  private String provider;

  private String refreshToken;

  @Builder
  public Member(String email, String password,String name, String nickname, String provider, String refreshToken) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.nickname = nickname;
    this.provider = provider;
    this.refreshToken = refreshToken;
  }

  public Member update(String name, String email) {
    this.name = name;
    this.email = email;
    return this;
  }
}
