package com.mangmuganae.eolmago.global.security.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class TokenExtractionException extends CustomException {

  public TokenExtractionException(ErrorCode errorCode) {
    super(errorCode);
  }
}
