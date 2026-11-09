package com.mangmuganae.eolmago.domain.training.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CriteriaRowMapper implements RowMapper<Criteria> {

  @Override
  public Criteria mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Criteria.builder()
        .id(rs.getLong("id"))
        .content(rs.getString("content"))
        .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
        .build();
  }

}
