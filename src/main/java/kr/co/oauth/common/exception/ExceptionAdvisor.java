package kr.co.oauth.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestControllerAdvice
public class ExceptionAdvisor {

  @ExceptionHandler(value = {CustomException.class})
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

    ErrorCodeEnum errorCodeEnum = e.getErrorCodeEnum();

    return ErrorResponse.toResponseEntity(errorCodeEnum);
  }

  // Valid 예외 핸들러
  @ExceptionHandler(value = {BindException.class})
  public ResponseEntity<ErrorResponse> handleBindException(HttpServletRequest request, BindException ex) {

    BindingResult bindingResult = ex.getBindingResult();
    StringBuilder sb = new StringBuilder();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      sb.append(fieldError.getField()).append(":");
      sb.append(fieldError.getDefaultMessage());
      sb.append(", ");
    }

    return ErrorResponse.toResponseEntityValid(sb.toString(), HttpStatus.BAD_REQUEST);

  }


}
