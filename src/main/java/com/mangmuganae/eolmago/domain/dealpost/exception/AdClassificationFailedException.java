package com.mangmuganae.eolmago.domain.dealpost.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class AdClassificationFailedException extends CustomException {

  public AdClassificationFailedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
