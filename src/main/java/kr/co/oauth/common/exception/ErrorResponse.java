package kr.co.oauth.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder

public class ErrorResponse {

  private int status;
  private String errorCode;
  private String errorMessage;


  public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCodeEnum errorCodeEnum) {

    return ResponseEntity
            .status(errorCodeEnum.getHttpStatus())
            .body(ErrorResponse.builder()
                    .errorCode(errorCodeEnum.getErrorCode())
                    .status(errorCodeEnum.getHttpStatus().value())
                    .errorMessage(errorCodeEnum.getMessage())
                    .build());
  }

  public static ResponseEntity<ErrorResponse> toResponseEntityValid(String errorCode, HttpStatus httpStatus) {

    return ResponseEntity
            .status(httpStatus.value())
            .body(ErrorResponse.builder()
                    .errorMessage(errorCode)
                    .status(httpStatus.value())
                    .build()
            );
  }
}
