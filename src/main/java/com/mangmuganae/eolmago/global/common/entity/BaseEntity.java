package com.mangmuganae.eolmago.global.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

  private static final String NOT_DELETED = "N";
  private static final String DELETED = "Y";

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedBy
  private LocalDateTime updatedAt;

  private String deleteYn;

  @PrePersist
  public void prePersist() {
    if (this.deleteYn == null) {
      this.deleteYn = NOT_DELETED;
    }
  }

  public void delete() {
    this.deleteYn = DELETED;
  }

  public boolean isDeleted() {
    return DELETED.equals(this.deleteYn);
  }

}
