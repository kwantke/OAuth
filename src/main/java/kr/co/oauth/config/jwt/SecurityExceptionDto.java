package kr.co.oauth.config.jwt;

import lombok.Getter;

@Getter
public class SecurityExceptionDto {

  private int statusCode;

  private String msg;

  public SecurityExceptionDto(int statusCode, String msg){
    this.statusCode = statusCode;
    this.msg = msg;
  }
}
