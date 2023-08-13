package kr.co.oauth.member;


import kr.co.oauth.common.Annotation.LogExecutionTime;
import kr.co.oauth.config.util.Message;
import kr.co.oauth.member.MemberService;
import kr.co.oauth.member.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/login")
  @LogExecutionTime
  public ResponseEntity<Message> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
    return null;
  }
}
