package com.mangmuganae.eolmago.domain.dealpost.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class DealPostSearchResponseDto {

  private List<DealPostPreivewResponseDto> dealPosts;

  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private boolean hasNext;

  @Builder
  private DealPostSearchResponseDto(List<DealPostPreivewResponseDto> dealPosts, int page, int size,
      long totalElements, int totalPages, boolean hasNext) {

    this.dealPosts = dealPosts;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.hasNext = hasNext;
  }

  public static DealPostSearchResponseDto of(Page<?> page,
      List<DealPostPreivewResponseDto> dealPosts) {

    return DealPostSearchResponseDto.builder()
        .dealPosts(dealPosts)
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .hasNext(page.hasNext())
        .build();
  }

}
