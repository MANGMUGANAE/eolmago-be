package com.mangmuganae.eolmago.domain.alert.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class DuplicatedAlertException extends CustomException {

  public DuplicatedAlertException(ErrorCode errorCode) {
    super(errorCode);
  }
}
