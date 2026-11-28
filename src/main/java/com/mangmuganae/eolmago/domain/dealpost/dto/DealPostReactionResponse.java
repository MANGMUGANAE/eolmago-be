package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.dealpostreaction.entity.ReactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPostReactionResponse {
  private ReactionType reactionType;
  private Long likeCount;
  private Long dislikeCount;

  @Builder
  private DealPostReactionResponse(ReactionType reactionType, Long likeCount, Long dislikeCount) {
    this.reactionType = reactionType;
    this.likeCount = likeCount;
    this.dislikeCount = dislikeCount;
  }

  public static DealPostReactionResponse of(ReactionType reactionType, Long likeCount, Long dislikeCount) {
    return DealPostReactionResponse.builder()
        .reactionType(reactionType)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .build();
  }
}
