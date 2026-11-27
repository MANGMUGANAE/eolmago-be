package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import com.mangmuganae.eolmago.domain.user.entity.JjandolRank;
import com.mangmuganae.eolmago.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DealPostDetailResponseDto {


  private List<String> imageFileUrlList;

  private String storeName;
  private String productName;

  private Integer productPrice;

  private UserInfo userInfo;
  private LocalDateTime createdAt;

  private String content;
  private String purchaseLink;

  private Long likeCount;
  private Long dislikeCount;
  private Long viewCount;

  private DealPostCommentResponseDto dealPostCommentResponseDto;

  @Getter
  @Builder
  public static class UserInfo {
    private String nickname;
    private String imageFileUrl;
    private JjandolRank jjandolRank;
  }

  public static DealPostDetailResponseDto createByDealPostAndUserAndComments(
      DealPost dealPost, User user, List<Comment> comments) {

    List<String> imageFileUrlList = dealPost.getImageFiles().stream()
        .map(ImageFile::getImageFileUrl).toList();

    UserInfo userInfo1 = UserInfo.builder()
        .nickname(user.getNickname())
        .imageFileUrl(user.getFile() != null ? user.getFile().getImageFileUrl() : null)
        .jjandolRank(user.getJjandolRank())
        .build();

    DealPostCommentResponseDto dealPostCommentResponseDto1 = DealPostCommentResponseDto.createByComments(
        comments
    );

    return DealPostDetailResponseDto.builder()
        .imageFileUrlList(imageFileUrlList)
        .storeName(dealPost.getStore() != null ? dealPost.getStore().getStoreName() : null)
        .productName(dealPost.getProductName())
        .productPrice(dealPost.getProductPrice())
        .userInfo(userInfo1)
        .createdAt(dealPost.getCreatedAt())
        .content(dealPost.getContent())
        .purchaseLink(dealPost.getPurchaseLink())
        .likeCount(dealPost.getLikeCount())
        .dislikeCount(dealPost.getDislikeCount())
        .viewCount(dealPost.getViewCount())
        .dealPostCommentResponseDto(dealPostCommentResponseDto1)
        .build();
  }
}
