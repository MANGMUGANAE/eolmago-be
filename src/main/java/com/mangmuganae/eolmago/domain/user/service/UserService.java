package com.mangmuganae.eolmago.domain.user.service;

import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.domain.imagefile.repository.ImageFileRepository;
import com.mangmuganae.eolmago.domain.user.dto.SignupRequest;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.exception.NicknameRequiredException;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.config.FileStorage;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final ImageFileRepository imageFileRepository;

  private final FileStorage fileStorage;

  @Transactional
  public void signup(SignupRequest request, MultipartFile file, String uuid) {

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    if (user.getNickname() != null) {
      return;
    }

    validateNickname(request.getNickname());

    if (hasFile(file)) {
      ImageFile imageFile = imageFileRepository.save(ImageFile.of(fileStorage.store(file)));
      user.updateProfileImage(imageFile);
    }

    user.updateProfileNickname(request.getNickname());
  }

  /**
   * 닉네임은 필수입니다. 비어 있으면 가입되지 않은 상태로 남으므로 저장 전에 막습니다.
   */
  private void validateNickname(String nickname) {

    if (nickname == null || nickname.isBlank()) {
      throw new NicknameRequiredException(ErrorCode.NICKNAME_REQUIRED);
    }
  }

  /**
   * 프로필 이미지는 선택입니다. 파트를 보내지 않거나 빈 파일이면 이미지 없이 가입합니다.
   */
  private boolean hasFile(MultipartFile file) {
    return file != null && !file.isEmpty();
  }
}
