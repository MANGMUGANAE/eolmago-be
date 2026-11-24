package com.mangmuganae.eolmago.global.exception;


import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.exception.InvalidRefrashJwtException;
import com.mangmuganae.eolmago.global.security.exception.TokenExtractionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDTO> handleException(Exception e) {
    log.error("처리되지 않은 예외 발생", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR));
  }

}
