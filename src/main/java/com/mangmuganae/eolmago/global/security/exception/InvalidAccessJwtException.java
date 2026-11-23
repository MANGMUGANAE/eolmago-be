package com.mangmuganae.eolmago.global.security.exception;


import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class InvalidAccessJwtException extends CustomException {

  public InvalidAccessJwtException(ErrorCode errorCode) {
    super(errorCode);
  }
}
