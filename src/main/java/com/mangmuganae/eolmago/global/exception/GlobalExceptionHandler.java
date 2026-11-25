package com.mangmuganae.eolmago.global.exception;


import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.exception.InvalidRefrashJwtException;
import com.mangmuganae.eolmago.global.security.exception.TokenExtractionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final int MIN_HTTP_STATUS_CODE = 100;
  private static final int MAX_HTTP_STATUS_CODE = 599;

  @ExceptionHandler(TokenExtractionException.class)
  public ResponseEntity<ResponseDTO> tokenExtractionException(TokenExtractionException e) {
    log.info(e.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ResponseDTO.of(ErrorCode.TOKEN_EXTRACTION_FAILED));
  }

  @ExceptionHandler(InvalidRefrashJwtException.class)
  public ResponseEntity<ResponseDTO> invalidRefrashJwtException(InvalidRefrashJwtException e) {
    log.info(e.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ResponseDTO.of(ErrorCode.REFRESH_TOKEN_INVALID));
  }

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ResponseDTO> customException(CustomException e) {
    log.info(e.getMessage());
    return ResponseEntity
        .status(httpStatusCodeOf(e.getStatusCode()))
        .body(ResponseDTO.of(e.getErrorCode()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDTO> handleException(Exception e) {
    log.error("처리되지 않은 예외 발생", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR));
  }

  /**
   * ErrorCode 에는 토큰 관련 커스텀 코드(999 ~ 1002)처럼 HTTP 상태코드가 아닌 값도 있습니다.
   * 이 경우 HTTP 상태는 401 로 내리고, 커스텀 코드는 응답 본문의 responseCode 로 전달합니다.
   */
  private HttpStatusCode httpStatusCodeOf(int statusCode) {

    if (statusCode < MIN_HTTP_STATUS_CODE || statusCode > MAX_HTTP_STATUS_CODE) {
      return HttpStatus.UNAUTHORIZED;
    }

    return HttpStatusCode.valueOf(statusCode);
  }

}
