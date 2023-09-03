package kr.co.oauth.member.service;

import kr.co.oauth.common.exception.CustomException;
import kr.co.oauth.common.exception.ErrorCodeEnum;
import kr.co.oauth.common.util.Message;

import kr.co.oauth.common.util.StatusEnum;
import kr.co.oauth.config.jwt.JwtUtil;
import kr.co.oauth.config.jwt.TokenDto;
import kr.co.oauth.member.dto.requestDto.CheckIdRequestDto;
import kr.co.oauth.member.dto.requestDto.LoginRequestDto;
import kr.co.oauth.member.dto.requestDto.SignupRequestDto;
import kr.co.oauth.member.dto.responseDto.LoginResponseDto;
import kr.co.oauth.member.entity.Member;
import kr.co.oauth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtUtil jwtUtil;

  private static final Queue<LoginRequestDto> loginQueue = new LinkedList<>();
  private static final Object lock = new Object();


  public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
    String userEmail = loginRequestDto.getEmail();
    String userPassword = loginRequestDto.getPassword();

    Member member = memberRepository.findByEmail(userEmail)
            .orElseThrow(()-> new CustomException(ErrorCodeEnum.ID_NOT_FOUND));

    if(!passwordEncoder.matches(userPassword,member.getPassword())){
      throw new CustomException(ErrorCodeEnum.INVALID_PASSWORD);
    }
    TokenDto tokenDto = jwtUtil.createAllToken(userEmail);

    member.updateRefreshToken(tokenDto.getRefreshToken());

    memberRepository.save(member);

    Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setDomain("www.kkk.com");
    response.addCookie(refreshTokenCookie);

    LoginResponseDto loginResponseDto = new LoginResponseDto(
            member, tokenDto.getAccessToken(), tokenDto.getRefreshToken()
    );
    Message message = Message.setSuccess(StatusEnum.OK,"success", loginResponseDto);

    return new ResponseEntity<>(message, HttpStatus.OK);
  }
  @Transactional
  public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto) {
    String email = signupRequestDto.getEmail();
    String password = passwordEncoder.encode(signupRequestDto.getPassword());
    String nickname = signupRequestDto.getNickname();

    if(memberRepository.findByEmail(email).isPresent()) {
      throw new CustomException(ErrorCodeEnum.DUPLICATE_IDENTIFIER);
    }

    Member member = Member.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .myImgUrl("https://danimdata.s3.ap-northeast-2.amazonaws.com/avatar.png")
            .provider("APP")
            .isDeleted(false)
            .build();

    memberRepository.saveAndFlush(member);

    Message message = Message.setSuccess(StatusEnum.OK, "success");
    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  //일반 회원가입 아이디 중복 검사
  @Transactional
  public ResponseEntity<Message> checkId(CheckIdRequestDto checkIdRequestDto) {

    Message message;
    String useYn;
    if(memberRepository.findByEmail(checkIdRequestDto.getEmail()).isPresent()) {
      useYn = "Y";
    } else {
      useYn = "N";
    }

    message = Message.setSuccess(StatusEnum.OK, useYn);
    return new ResponseEntity<>(message, HttpStatus.OK);
  }
}
