package com.mangmuganae.eolmago.domain.training.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Evaluator {

  private Long id;
  private String name;
  private String pin;
  private LocalDateTime createdAt;

}
