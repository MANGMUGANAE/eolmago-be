package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.user.entity.JjandolRank;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private Long commentId;
    private Long parentCommentId;
    private UserInfo userInfo;
    private CommentInfo commentInfo;

    @Schema(description = "이 댓글에 달린 대댓글입니다. 대댓글에는 항상 빈 배열입니다.")
    private List<Comment> replies;
  }

  public static DealPostCommentResponseDto createByComments(
      List<com.mangmuganae.eolmago.domain.comment.entity.Comment> comments) {

    Map<Long, List<Comment>> repliesByParentId = new LinkedHashMap<>();
    List<Comment> parents = new ArrayList<>();

    comments.stream()
        .sorted((left, right) -> Long.compare(left.getId(), right.getId()))
        .forEach(comment -> {
          Long parentCommentId = comment.getComment() == null
              ? null
              : comment.getComment().getId();

          Comment converted = convert(comment, parentCommentId);

          if (parentCommentId == null) {
            parents.add(converted);
          } else {
            repliesByParentId
                .computeIfAbsent(parentCommentId, key -> new ArrayList<>())
                .add(converted);
          }
        });

    List<Comment> result = parents.stream()
        .map(parent -> Comment.builder()
            .commentId(parent.getCommentId())
            .parentCommentId(null)
            .userInfo(parent.getUserInfo())
            .commentInfo(parent.getCommentInfo())
            .replies(repliesByParentId.getOrDefault(parent.getCommentId(), List.of()))
            .build())
        .toList();

    return DealPostCommentResponseDto.builder()
        .comments(result)
        .commentCount(comments.size())
        .build();
  }

  private static Comment convert(
      com.mangmuganae.eolmago.domain.comment.entity.Comment comment, Long parentCommentId) {

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

    return Comment.builder()
        .commentId(comment.getId())
        .parentCommentId(parentCommentId)
        .userInfo(userInfo)
        .commentInfo(commentInfo)
        .replies(List.of())
        .build();
  }

}
