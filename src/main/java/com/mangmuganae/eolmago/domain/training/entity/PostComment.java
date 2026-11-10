package com.mangmuganae.eolmago.domain.training.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostComment {

  private Long id;
  private Long postId;
  private Long evaluatorId;
  private String evaluatorName;
  private String content;
  private LocalDateTime createdAt;

}
