package com.mangmuganae.eolmago.domain.user.entity;


import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

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

  /**
   * 짠돌 등급을 정렬 가능한 숫자로 환산한 읽기 전용 값입니다.
   * jjandol_rank 가 문자열로 저장되어 등급 순 정렬이 불가능하므로 조회 시 DB 에서 계산합니다.
   * 값은 JjandolRank.level 과 동일하게 유지해야 합니다.
   */
  @Formula("(CASE jjandol_rank "
      + "WHEN 'STARTER' THEN 1 "
      + "WHEN 'BEGINNER' THEN 2 "
      + "WHEN 'INTERMEDIATE' THEN 3 "
      + "WHEN 'EXPERT' THEN 4 "
      + "WHEN 'MASTER' THEN 5 "
      + "ELSE 0 END)")
  private Integer jjandolRankLevel;

  private Integer jjandolCoin;

  private String nickname;

  private Integer reportedCount; //누적 신고받은 회수
  private Integer likeRate; //누적 좋아요 비율

  @Enumerated(EnumType.STRING)
  private Role role;

  private Double userReliabillityScore; //사용자 신뢰도

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_file_id", unique = true)
  private ImageFile file;

  @OneToMany(mappedBy = "user")
  private List<Comment> comments = new ArrayList<>();


  @Builder
  private User(Long id, String uuid, String providerId, String socialProvider,
      JjandolRank jjandolRank, Integer jjandolCoin, String nickname, Integer reportedCount,
      Integer likeRate, Role role, Double userReliabillityScore,
      ImageFile file) {

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
    this.file = file;
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
        .userReliabillityScore(0.0)
        .file(null)
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
        .userReliabillityScore(0.0)
        .file(null)
        .build();
  }

  public void updateProfileImage(ImageFile imageFile) {
    this.file = imageFile;
  }
  public void updateProfileNickname(String nickname) {
    this.nickname = nickname;
  }

}
