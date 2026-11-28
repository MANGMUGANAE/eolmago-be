package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.user.entity.JjandolRank;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPostCommentResponseDto {

  private List<Comment> comments;
  private Integer commentCount;

  @Builder
  private DealPostCommentResponseDto(List<Comment> comments, Integer commentCount) {
    this.comments = comments;
    this.commentCount = commentCount;
  }

  @Getter
  @Builder
  @Schema(name = "CommentUserInfo")
  public static class UserInfo {
    private String nickname;
    private String imageFileUrl;
    private JjandolRank jjandolRank;
  }

  @Getter
  @Builder
  public static class CommentInfo {
    private String content;
    private LocalDateTime createdAt;
  }

  @Getter
  @Builder
  public static class Comment {
    private UserInfo userInfo;
    private CommentInfo commentInfo;
  }


  public static DealPostCommentResponseDto createByComments(List<com.mangmuganae.eolmago.domain.comment.entity.Comment> comments) {
    List<Comment> comments1 = comments.stream()
        .map(comment -> {

          UserInfo userInfo = UserInfo.builder()
              .nickname(comment.getUser().getNickname())
              .imageFileUrl(comment.getUser().getFile() != null
                  ? comment.getUser().getFile().getImageFileUrl()
                  : null)
              .jjandolRank(comment.getUser().getJjandolRank())
              .build();

          CommentInfo commentInfo = CommentInfo.builder()
              .content(comment.getContent())
              .createdAt(comment.getCreatedAt())
              .build();

          Comment comment1 = Comment.builder()
              .userInfo(userInfo)
              .commentInfo(commentInfo)
              .build();

          return comment1;
        }).toList();

    DealPostCommentResponseDto response = DealPostCommentResponseDto.builder()
        .comments(comments1)
        .commentCount(comments1.size())
        .build();

    return response;
  }


}
