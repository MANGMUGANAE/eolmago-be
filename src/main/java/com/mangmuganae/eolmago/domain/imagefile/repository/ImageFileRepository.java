package com.mangmuganae.eolmago.domain.imagefile.repository;

import com.mangmuganae.eolmago.domain.imagefile.entity.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

}
