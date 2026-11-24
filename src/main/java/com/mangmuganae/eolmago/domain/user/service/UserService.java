package com.mangmuganae.eolmago.domain.user.service;

import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.domain.imagefile.repository.ImageFileRepository;
import com.mangmuganae.eolmago.domain.user.dto.SignupRequest;
import com.mangmuganae.eolmago.domain.user.entity.User;
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

    ImageFile imageFile = imageFileRepository.save(ImageFile.of(fileStorage.store(file)));
    user.updateProfileImage(imageFile);
    user.updateProfileNickname(request.getNickname());
  }
}
