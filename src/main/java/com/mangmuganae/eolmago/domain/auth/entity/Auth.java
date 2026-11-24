package com.mangmuganae.eolmago.domain.auth.entity;


import com.mangmuganae.eolmago.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;

  private String refreshToken;
  @Builder
  private Auth(Long id, String uuid, String refreshToken) {
    this.id = id;
    this.uuid = uuid;
    this.refreshToken = refreshToken;
  }

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public static Auth of(String uuid, String refreshToken) {

    return Auth.builder()
        .id(null)
        .uuid(uuid)
        .refreshToken(refreshToken)
        .build();
  }




}
