package com.mangmuganae.eolmago.domain.imagefile.entity;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "image_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imageFileUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_post_id")
  private DealPost dealPost;

  @Builder
  public ImageFile(Long id, String imageFileUrl, DealPost dealPost) {
    this.id = id;
    this.imageFileUrl = imageFileUrl;
    this.dealPost = dealPost;
  }

  public static ImageFile of(String imageFileUrl) {
    return ImageFile.builder()
        .id(null)
        .imageFileUrl(imageFileUrl)
        .dealPost(null)
        .build();
  }

  public static ImageFile of(String imageFileUrl, DealPost dealPost) {
    return ImageFile.builder()
        .id(null)
        .imageFileUrl(imageFileUrl)
        .dealPost(dealPost)
        .build();
  }

}
