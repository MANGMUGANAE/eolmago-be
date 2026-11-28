package com.mangmuganae.eolmago.domain.reportpost.repository;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.reportpost.entity.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {

  @Query("select count(*) from ReportPost r where r.dealPost = :dealPost")
  long findReportPostCountByDealPost(DealPost dealPost);

}
