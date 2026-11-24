package com.mangmuganae.eolmago.global.security.exception;


import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class InvalidRefrashJwtException extends CustomException {

  public InvalidRefrashJwtException(ErrorCode errorCode) {
    super(errorCode);
  }
}
