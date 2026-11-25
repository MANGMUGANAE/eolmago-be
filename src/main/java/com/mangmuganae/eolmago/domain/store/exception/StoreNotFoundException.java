package com.mangmuganae.eolmago.domain.store.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class StoreNotFoundException extends CustomException {

  public StoreNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
