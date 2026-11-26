package com.mangmuganae.eolmago.domain.imagefile.repository;

import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

  /**
   * 핫딜 글마다 가장 먼저 등록된 이미지 한 장씩을 조회합니다. 목록 썸네일로 사용합니다.
   * 글 단위로 한 번씩 조회하지 않도록 페이지의 글 전체를 한 번에 가져옵니다.
   */
  @Query("""
      select imageFile from ImageFile imageFile
      where imageFile.dealPost.id in :dealPostIds
        and imageFile.id = (
          select min(firstImageFile.id) from ImageFile firstImageFile
          where firstImageFile.dealPost.id = imageFile.dealPost.id
        )
      """)
  List<ImageFile> findFirstImageFilesByDealPostIds(
      @Param("dealPostIds") Collection<Long> dealPostIds);

}
