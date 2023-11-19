package kr.co.oauth.user.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.Set;

@Getter
@Setter
public class SignupRequestDto {

  @Pattern(regexp = "^[a-z0-9_+.-]+@[a-z0-9-]+\\.[a-z0-9]{1,20}$", message="아이디는 올바른 이메일 형식으로 입력해주세요. (ex-name77@naver.com)")
  private String email;

  @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d!@#$%^&*()_-]{5,12}$", message = "비밀번호는 5~12자 이내 영어(소문자),숫자,특수기호(선택) 범위에서 입력해야합니다.")
  private String password;

  private String name;

  private Set<String> role;

  private String nickname;

}
