package kr.co.oauth.user.service;

import kr.co.oauth.common.exception.CustomException;
import kr.co.oauth.common.exception.ErrorCodeEnum;
import kr.co.oauth.common.util.Message;

import kr.co.oauth.common.util.StatusEnum;
import kr.co.oauth.config.jwt.JwtUtil;
import kr.co.oauth.config.jwt.TokenDto;
import kr.co.oauth.user.dto.requestDto.CheckIdRequestDto;
import kr.co.oauth.user.dto.requestDto.LoginRequestDto;
import kr.co.oauth.user.dto.requestDto.SignupRequestDto;
import kr.co.oauth.user.dto.responseDto.LoginResponseDto;
import kr.co.oauth.user.entity.ERole;
import kr.co.oauth.user.entity.User;
import kr.co.oauth.user.entity.Role;
import kr.co.oauth.user.repository.UserRepository;
import kr.co.oauth.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  private final JwtUtil jwtUtil;

  private static final Queue<LoginRequestDto> loginQueue = new LinkedList<>();
  private static final Object lock = new Object();


  public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
    String userEmail = loginRequestDto.getEmail();
    String userPassword = loginRequestDto.getPassword();

    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(()-> new CustomException(ErrorCodeEnum.ID_NOT_FOUND));

    if(!passwordEncoder.matches(userPassword, user.getPassword())){
      throw new CustomException(ErrorCodeEnum.INVALID_PASSWORD);
    }
    TokenDto tokenDto = jwtUtil.createAllToken(userEmail);

    user.updateRefreshToken(tokenDto.getRefreshToken());

    userRepository.save(user);

    Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(true);
    refreshTokenCookie.setDomain("www.kkk.com");
    response.addCookie(refreshTokenCookie);

    LoginResponseDto loginResponseDto = new LoginResponseDto(
            user, tokenDto.getAccessToken(), tokenDto.getRefreshToken()
    );
    Message message = Message.setSuccess(StatusEnum.OK,"success", loginResponseDto);

    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  //회원가입
  @Transactional
  public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto) {
    String email = signupRequestDto.getEmail();
    String password = passwordEncoder.encode(signupRequestDto.getPassword());
    String name = signupRequestDto.getName();
    String nickname = signupRequestDto.getNickname();

    if(userRepository.findByEmail(email).isPresent()) {
      throw new CustomException(ErrorCodeEnum.DUPLICATE_IDENTIFIER);
    }

    Set<String> strRoles = signupRequestDto.getRole();
    Set<Role>  roles = new HashSet<>();

    if(strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new CustomException(ErrorCodeEnum.ROLE_NOT_FOUND));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(()-> new CustomException(ErrorCodeEnum.ROLE_NOT_FOUND));
            roles.add(adminRole);

            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new CustomException(ErrorCodeEnum.ROLE_NOT_FOUND));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new CustomException(ErrorCodeEnum.ROLE_NOT_FOUND));
            roles.add(userRole);
        }
      });
    }


    User user = User.builder()
            .email(email)
            .password(password)
            .name(name)
            .roles(roles)
            .nickname(nickname)
            .myImgUrl("https://danimdata.s3.ap-northeast-2.amazonaws.com/avatar.png")
            .provider("APP")
            .isDeleted(false)
            .build();

    userRepository.saveAndFlush(user);

    Message message = Message.setSuccess(StatusEnum.OK, "success");
    return new ResponseEntity<>(message, HttpStatus.OK);
  }

  //일반 회원가입 아이디 중복 검사
  @Transactional
  public ResponseEntity<Message> checkId(CheckIdRequestDto checkIdRequestDto) {

    Message message;
    String useYn;
    if(userRepository.findByEmail(checkIdRequestDto.getEmail()).isPresent()) {
      useYn = "Y";
    } else {
      useYn = "N";
    }

    message = Message.setSuccess(StatusEnum.OK, useYn);
    return new ResponseEntity<>(message, HttpStatus.OK);
  }
}
