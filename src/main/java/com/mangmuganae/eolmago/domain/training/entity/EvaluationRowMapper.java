package com.mangmuganae.eolmago.domain.training.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EvaluationRowMapper implements RowMapper<Evaluation> {

  @Override
  public Evaluation mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Evaluation.builder()
        .id(rs.getLong("id"))
        .postId(rs.getLong("post_id"))
        .evaluatorId(rs.getLong("evaluator_id"))
        .evaluatorName(rs.getString("evaluator_name"))
        .label(rs.getInt("label"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .build();
  }

}
