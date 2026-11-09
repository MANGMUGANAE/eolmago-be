package com.mangmuganae.eolmago.domain.training.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Criteria {

  private Long id;
  private String content;
  private LocalDateTime updatedAt;

}
