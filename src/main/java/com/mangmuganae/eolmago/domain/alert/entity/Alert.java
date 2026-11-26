package com.mangmuganae.eolmago.domain.alert.entity;

import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "alert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String keyword; //대상 키워드

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @Builder
  private Alert(Long id, String keyword, User user, Category category) {
    this.id = id;
    this.keyword = keyword;
    this.user = user;
    this.category = category;
  }

  public static Alert of(String keyword, User user, Category category) {
    return Alert.builder()
        .id(null)
        .keyword(keyword)
        .user(user)
        .category(category)
        .build();
  }
}
