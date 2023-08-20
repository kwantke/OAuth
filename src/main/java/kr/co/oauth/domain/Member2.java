package kr.co.oauth.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Entity
@Getter
@Table(name = "Member2")
public class Member2 {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name= "member_id")
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name= "email", nullable=false)
    private String email;

    @Column(name="provider", nullable=false)
    private String provider;

    @Column(name="nickname", nullable=true, unique=true)
    private String nickname;


    @Builder
    public Member2(Long id, String name, String email, String provider, String nickname){

        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.nickname = nickname;
    }

    public Member2 update(String name, String email){
        this.name = name;
        this.email = email;
        return this;
    }
}
