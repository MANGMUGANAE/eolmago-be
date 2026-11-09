package com.mangmuganae.eolmago.domain.training.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EvaluatorRowMapper implements RowMapper<Evaluator> {

  @Override
  public Evaluator mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Evaluator.builder()
        .id(rs.getLong("id"))
        .name(rs.getString("name"))
        .pin(rs.getString("pin"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .build();
  }

}
