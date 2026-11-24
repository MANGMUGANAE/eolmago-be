package com.mangmuganae.eolmago.domain.imagefile.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  @Builder
  public ImageFile(Long id, String imageFileUrl) {
    this.id = id;
    this.imageFileUrl = imageFileUrl;
  }

  public static ImageFile of(String imageFileUrl) {
    return ImageFile.builder()
        .id(null)
        .imageFileUrl(imageFileUrl)
        .build();
  }

}
