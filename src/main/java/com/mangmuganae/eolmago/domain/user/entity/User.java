package com.mangmuganae.eolmago.domain.user.entity;


import com.mangmuganae.eolmago.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;
  private String providerId;

  private String socialProvider;

  @Enumerated(EnumType.STRING)
  private JjandolRank jjandolRank;

  private Integer jjandolCoin;

  private String nickname;

  private Integer reportedCount; //누적 신고받은 회수
  private Integer likeRate; //누적 좋아요 비율

  @Enumerated(EnumType.STRING)
  private Role role;

  private Integer userReliabillityScore; //사용자 신뢰도

  @Builder
  private User(Long id, String uuid, String providerId, String socialProvider,
      JjandolRank jjandolRank, Integer jjandolCoin, String nickname, Integer reportedCount,
      Integer likeRate, Role role, Integer userReliabillityScore) {

    this.id = id;
    this.uuid = uuid;
    this.providerId = providerId;
    this.socialProvider = socialProvider;
    this.jjandolRank = jjandolRank;
    this.jjandolCoin = jjandolCoin;
    this.nickname = nickname;
    this.reportedCount = reportedCount;
    this.likeRate = likeRate;
    this.role = role;
    this.userReliabillityScore = userReliabillityScore;
  }

  public static User createMember(String providerId, String socialProvider,
      String nickname) {
    return User.builder()
        .id(null)
        .uuid(UUID.randomUUID().toString())
        .providerId(providerId)
        .socialProvider(socialProvider)
        .jjandolRank(JjandolRank.BEGINNER)
        .jjandolCoin(0)
        .nickname(nickname)
        .reportedCount(0)
        .likeRate(0)
        .role(Role.MEMBER)
        .userReliabillityScore(0)
        .build();
  }

  public static User createAdmin(String providerId, String socialProvider,
      String nickname) {

    return User.builder()
        .id(null)
        .uuid(UUID.randomUUID().toString())
        .providerId(providerId)
        .socialProvider(socialProvider)
        .jjandolRank(JjandolRank.BEGINNER)
        .jjandolCoin(0)
        .nickname(nickname)
        .reportedCount(0)
        .likeRate(0)
        .role(Role.ADMIN)
        .userReliabillityScore(0)
        .build();
  }

}
