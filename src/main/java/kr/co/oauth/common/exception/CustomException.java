package kr.co.oauth.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final ErrorCodeEnum errorCodeEnum;

  public CustomException(ErrorCodeEnum errorCodeEnum) {
    super(errorCodeEnum.getMessage());
    this.errorCodeEnum = errorCodeEnum;
  }
}
