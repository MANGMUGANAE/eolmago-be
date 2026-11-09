package com.mangmuganae.eolmago.domain.training.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Evaluation {

  public static final int LABEL_NORMAL = 0;
  public static final int LABEL_AD = 1;
  public static final int LABEL_AMBIGUOUS = 2;

  private Long id;
  private Long postId;
  private Long evaluatorId;
  private String evaluatorName;
  private int label;
  private LocalDateTime createdAt;

}
