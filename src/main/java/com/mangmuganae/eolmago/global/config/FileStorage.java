package com.mangmuganae.eolmago.global.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.exception.FileUploadFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FileStorage {

  @Value("${file.upload-path}")
  private String uploadPath;

  public String store(MultipartFile file) {

    System.out.println("원본 파일명: " + file.getOriginalFilename());
    System.out.println("Content-Type: " + file.getContentType());

    String extension = extractExtension(file.getOriginalFilename());
    String savedName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

    Path target = Paths.get(uploadPath).toAbsolutePath().resolve(savedName);

    try {
      Files.createDirectories(target.getParent());
      file.transferTo(target.toFile());
    } catch (IOException e) {
      log.error("파일 저장 실패: {}", file.getOriginalFilename(), e);
      throw new FileUploadFailedException(ErrorCode.FILE_UPLOAD_FAILED);
    }

    return "/files/" + savedName;
  }

  private String extractExtension(String filename) {
    if (filename == null) return "";
    int idx = filename.lastIndexOf('.');
    return idx == -1 ? "" : filename.substring(idx + 1);
  }
}