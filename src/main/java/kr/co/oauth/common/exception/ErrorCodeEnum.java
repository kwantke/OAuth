package kr.co.oauth.common.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCodeEnum {

  UPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다."),
  DUPLICATE_IDENTIFIER(HttpStatus.BAD_REQUEST, "ERROR_00001", "중복된 아이디 입니다."),
  ID_NOT_FOUND(HttpStatus.NOT_FOUND,"ERROR_00002","등록되지 않은 아이디 입니다."),
  INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"ERROR_00003","잘못된 비밀번호 입니다."),

  ROLE_NOT_FOUND(HttpStatus.NOT_FOUND,"ERROR_00004","권한이 존재하지 않습니다.");
  private final HttpStatus httpStatus;
  private final String errorCode;
  private String message;

  ErrorCodeEnum(HttpStatus httpStatus, String errorCode) {
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }

  ErrorCodeEnum(HttpStatus httpStatus, String errorCode, String message) {
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
    this.message = message;
  }
}
