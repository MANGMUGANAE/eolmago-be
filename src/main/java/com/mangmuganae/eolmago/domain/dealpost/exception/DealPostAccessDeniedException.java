package com.mangmuganae.eolmago.domain.dealpost.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class DealPostAccessDeniedException extends CustomException {

  public DealPostAccessDeniedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
