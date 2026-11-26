package com.mangmuganae.eolmago.domain.notification.config;

import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import com.mangmuganae.eolmago.domain.alert.repository.AlertRepository;
import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.repository.DealPostRepository;
import com.mangmuganae.eolmago.domain.notification.dto.NotificationWaitingObject;
import com.mangmuganae.eolmago.domain.notification.entity.Notification;
import com.mangmuganae.eolmago.domain.notification.entity.NotificationType;
import com.mangmuganae.eolmago.domain.notification.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWaitingManage {

  private final AlertRepository alertRepository;
  private final NotificationRepository notificationRepository;
  private final DealPostRepository dealPostRepository;
  private final Queue<NotificationWaitingObject> notificationWaitingObjects = new ConcurrentLinkedQueue<>();

  public void add(NotificationWaitingObject notificationWaitingObject) {
    this.notificationWaitingObjects.add(notificationWaitingObject);
  }


  //
  @Scheduled(fixedDelay = 2000)
  public void processWaitingNotifications() {

    for (NotificationWaitingObject notificationWaitingObject:this.notificationWaitingObjects) {
      DealPost dealPost = dealPostRepository.findById(notificationWaitingObject.getDealPost()
          .getId()).orElse(null);
      if(dealPost.isVerification()) {
        this.sendDealNotifications(notificationWaitingObject);
        this.notificationWaitingObjects.poll();
      }
    }

  }

  @Async
  public void sendDealNotifications(NotificationWaitingObject notificationWaitingObject) {

    List<Alert> alertWithoutCategory = alertRepository.findAllByCategoryIdIsNull(); //키워드
    List<Alert> alertWithoutKeyword = alertRepository.findAllByKeywordIsNull(); //카테고리

    List<Notification> notificationWithoutCategory = new ArrayList<>();
    List<Notification> notificationWithoutKeyword = new ArrayList<>();

    String keyword = notificationWaitingObject.getKeyword();
    DealPost dealPost = notificationWaitingObject.getDealPost();
    Category category = notificationWaitingObject.getCategory();

    //키워드 전송
    if (!alertWithoutCategory.isEmpty()) {
      for (Alert alert : alertWithoutCategory) {
        if (keyword.contains(alert.getKeyword())) {
          notificationWithoutCategory.add(
              Notification.of(keyword.substring(1, 12) + "..." + "핫딜이 올라왔어요",
                  NotificationType.KEYWORD,
                  alert.getUser(), dealPost, null)
          );
        }
      }
      notificationRepository.saveAll(notificationWithoutCategory);
    }

    //카테고리 전송
    if (!alertWithoutKeyword.isEmpty()) {
      for (Alert alert : alertWithoutKeyword) {
        if (alert.getCategory().equals(category)) {
          notificationWithoutKeyword.add(
              Notification.of(keyword.substring(0, 30) + "..." + "핫딜이 올라왔어요",
                  NotificationType.CATEGORY,
                  alert.getUser(), dealPost, null)
          );
        }
      }
      notificationRepository.saveAll(notificationWithoutKeyword);
    }


  }
}
