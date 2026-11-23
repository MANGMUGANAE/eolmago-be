package com.mangmuganae.eolmago.domain.user.repository;

import com.mangmuganae.eolmago.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
