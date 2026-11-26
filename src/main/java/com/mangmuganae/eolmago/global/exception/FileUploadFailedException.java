package com.mangmuganae.eolmago.global.exception;

public class FileUploadFailedException extends CustomException {

  public FileUploadFailedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
