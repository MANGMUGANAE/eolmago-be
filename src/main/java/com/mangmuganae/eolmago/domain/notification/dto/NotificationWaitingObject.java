package com.mangmuganae.eolmago.domain.notification.dto;

import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationWaitingObject {

  private Category category;
  private String keyword;
  private DealPost dealPost;
  private User user;

  @Builder
  private NotificationWaitingObject(Category category, String keyword, DealPost dealPost, User user) {
    this.category = category;
    this.keyword = keyword;
    this.dealPost = dealPost;
    this.user = user;
  }

  public NotificationWaitingObject of(Category category, String keyword, DealPost dealPost, User user) {
    return NotificationWaitingObject.builder()
        .category(category)
        .keyword(keyword)
        .dealPost(dealPost)
        .user(user)
        .build();
  }

}
