package com.mangmuganae.eolmago.domain.category.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CategoryNotFoundException extends CustomException {

  public CategoryNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
