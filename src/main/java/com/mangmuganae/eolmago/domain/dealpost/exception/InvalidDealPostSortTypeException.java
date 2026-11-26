package com.mangmuganae.eolmago.domain.dealpost.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class InvalidDealPostSortTypeException extends CustomException {

  public InvalidDealPostSortTypeException(ErrorCode errorCode) {
    super(errorCode);
  }
}
