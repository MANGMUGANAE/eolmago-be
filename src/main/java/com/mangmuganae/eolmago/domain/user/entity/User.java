package com.mangmuganae.eolmago.domain.user.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {

  private static final Logger log = LoggerFactory.getLogger(User.class);

  private Long id;
  private String name;

  private User(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public static User of(Long id, String name) {
    if (id == null || name == null) {
      log.info("id 또는 name 필드는 null일 수 없습니다. id: {}, name: {}", id, name);
      throw new IllegalArgumentException("id 또는 name 필드는 null일 수 없습니다.");
    }
    return new User(id, name);
  }

}
