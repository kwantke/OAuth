package kr.co.oauth.user;


import kr.co.oauth.common.Annotation.LogExecutionTime;
import kr.co.oauth.common.util.Message;
import kr.co.oauth.user.dto.requestDto.CheckIdRequestDto;
import kr.co.oauth.user.dto.requestDto.LoginRequestDto;
import kr.co.oauth.user.dto.requestDto.SignupRequestDto;
import kr.co.oauth.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor //@Autowired를 대신하여 사용합다.
public class UserController {

  private final UserService userService;

  @PostMapping("/users/login")
  @LogExecutionTime
  public ResponseEntity<Message> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
    return userService.login(loginRequestDto,response);
  }

  @PostMapping("/users/signup")
  @LogExecutionTime
  public ResponseEntity<Message> signUp(@Valid @RequestBody SignupRequestDto signupRequestDto) {
    return userService.signup(signupRequestDto);
  }

  //일반 회원가입 아이디 중북 검사
  @PostMapping("/users/checkId")
  @LogExecutionTime
  public ResponseEntity<Message> checkId(@Valid @RequestBody CheckIdRequestDto checkIdRequestDto) {

    return userService.checkId(checkIdRequestDto);
  }



}
