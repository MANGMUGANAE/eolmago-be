package com.mangmuganae.eolmago.domain.dealpost.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class ImageFileCountExceededException extends CustomException {

  public ImageFileCountExceededException(ErrorCode errorCode) {
    super(errorCode);
  }
}
