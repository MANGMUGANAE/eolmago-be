package com.mangmuganae.eolmago.domain.user.repository;

import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.entity.UserRowMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<User> userRowMapper = new UserRowMapper();

  public UserRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<User> findById(Long id) {
    String sql = "select * from user where id = ?";
    return jdbcTemplate.query(sql, userRowMapper, id)
        .stream()
        .findFirst();
  }

  public List<User> findAll() {
    String sql = "select * from user";
    return jdbcTemplate.query(sql, userRowMapper);
  }

}
