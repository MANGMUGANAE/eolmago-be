package com.mangmuganae.eolmago.domain.comment.service;

import com.mangmuganae.eolmago.domain.comment.dto.NotificationDto;
import com.mangmuganae.eolmago.domain.notification.entity.Notification;
import com.mangmuganae.eolmago.domain.notification.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public List<NotificationDto> findAllByUserId(String uuid) {


    List<NotificationDto> notificationDtos = notificationRepository.findAllByUuid(uuid).stream()
        .map(Notification::toDto)
        .toList();

    return notificationDtos;
  }
}
