package com.mangmuganae.eolmago.domain.user.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String uuid;
  private String socialId;

  @Enumerated(EnumType.STRING)
  private SocialProvider socialProvider;

  @Enumerated(EnumType.STRING)
  private JjandolRank jjandolRank;

  private Integer jjandolCoin;

  private String nickname;

  private Integer reportedCount; //누적 신고받은 회수
  private Integer likeRate; //누적 좋아요 비율

  private Role role;

  private Integer userReliabillityScore; //사용자 신뢰도

  @Builder
  private User(Long id, String uuid, String socialId, SocialProvider socialProvider,
      JjandolRank jjandolRank, Integer jjandolCoin, String nickname, Integer reportedCount,
      Integer likeRate, Role role, Integer userReliabillityScore) {

    this.id = id;
    this.uuid = uuid;
    this.socialId = socialId;
    this.socialProvider = socialProvider;
    this.jjandolRank = jjandolRank;
    this.jjandolCoin = jjandolCoin;
    this.nickname = nickname;
    this.reportedCount = reportedCount;
    this.likeRate = likeRate;
    this.role = role;
    this.userReliabillityScore = userReliabillityScore;
  }

  public static User createMember(String socialId, SocialProvider socialProvider,
      String nickname, Integer reportedCount, Integer likeRate) {
    return User.builder()
        .id(null)
        .uuid(UUID.randomUUID().toString())
        .socialId(socialId)
        .socialProvider(socialProvider)
        .jjandolRank(JjandolRank.BEGINNER)
        .jjandolCoin(0)
        .nickname(nickname)
        .reportedCount(reportedCount)
        .likeRate(likeRate)
        .role(Role.MEMBER)
        .userReliabillityScore(0)
        .build();
  }

  public static User createAdmin(String socialId, SocialProvider socialProvider,
      String nickname, Integer reportedCount, Integer likeRate) {

    return User.builder()
        .id(null)
        .uuid(UUID.randomUUID().toString())
        .socialId(socialId)
        .socialProvider(socialProvider)
        .jjandolRank(JjandolRank.BEGINNER)
        .jjandolCoin(0)
        .nickname(nickname)
        .reportedCount(reportedCount)
        .likeRate(likeRate)
        .role(Role.ADMIN)
        .userReliabillityScore(0)
        .build();
  }

}
