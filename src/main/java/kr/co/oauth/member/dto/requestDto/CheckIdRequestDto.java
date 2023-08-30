package kr.co.oauth.member.dto.requestDto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class CheckIdRequestDto {

  @Pattern(regexp = "^[a-z0-9_+.-]+@[a-z0-9-]+\\.[a-z0-9]{1,20}$", message="아이디는 올바른 이메일 형식으로 입력해주세요. (ex-name77@naver.com)")
  private String email;

}
