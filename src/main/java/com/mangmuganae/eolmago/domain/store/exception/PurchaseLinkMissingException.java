package com.mangmuganae.eolmago.domain.store.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class PurchaseLinkMissingException extends CustomException {

  public PurchaseLinkMissingException(ErrorCode errorCode) {
    super(errorCode);
  }
}
