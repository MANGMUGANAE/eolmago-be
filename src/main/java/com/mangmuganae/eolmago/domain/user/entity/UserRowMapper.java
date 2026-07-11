package com.mangmuganae.eolmago.domain.user.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    Long id = rs.getLong("id");
    String name = rs.getString("name");
    return User.of(id, name);
  }

}
