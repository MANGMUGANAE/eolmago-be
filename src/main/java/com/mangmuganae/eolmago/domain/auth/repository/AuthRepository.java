package com.mangmuganae.eolmago.domain.auth.repository;

import com.mangmuganae.eolmago.domain.auth.entity.Auth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {

  Optional<Auth> findByUuid(String uuid);

}
