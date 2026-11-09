package com.mangmuganae.eolmago.domain.training.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotdealPost {

  public static final String STATUS_PENDING = "PENDING";
  public static final String STATUS_COMPLETED = "COMPLETED";

  private Long id;
  private String title;
  private String content;
  private String contentHash;
  private String status;
  private Integer finalLabel;
  private LocalDateTime createdAt;

}
