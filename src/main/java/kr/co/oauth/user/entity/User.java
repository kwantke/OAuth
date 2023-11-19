package kr.co.oauth.user.entity;

import kr.co.oauth.common.entity.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "USERS",
       uniqueConstraints = {
          @UniqueConstraint(columnNames = "email")
       })
public class User extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name= "email", nullable=false)
  private String email;
  @Column(nullable = true)
  private String password;
  private String name;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "USER_ROLE",
             joinColumns = @JoinColumn(name = "user_id"),
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();
  private String nickname;
  private String myImgUrl;
  private String provider;

  private Boolean isDeleted; // 탈퇴 여부
  private String refreshToken;

  @Builder
  public User(String email, String password, String name, Set<Role> roles, String nickname, String myImgUrl, String provider, Boolean isDeleted, String refreshToken) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.roles = roles;
    this.nickname = nickname;
    this.myImgUrl = myImgUrl;
    this.provider = provider;
    this.isDeleted = isDeleted;
    this.refreshToken = refreshToken;
  }

  public User update(String name, String email, String refreshToken) {
    this.name = name;
    this.email = email;
    this.refreshToken = refreshToken;
    return this;
  }

  public User updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
    return this;
  }
}
