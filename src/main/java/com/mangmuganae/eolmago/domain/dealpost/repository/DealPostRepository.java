package com.mangmuganae.eolmago.domain.dealpost.repository;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealPostRepository extends JpaRepository<DealPost, Long> {

  long countByUser(User user);

}
