package com.mangmuganae.eolmago.domain.training.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PostCommentRowMapper implements RowMapper<PostComment> {

  @Override
  public PostComment mapRow(ResultSet rs, int rowNum) throws SQLException {
    return PostComment.builder()
        .id(rs.getLong("id"))
        .postId(rs.getLong("post_id"))
        .evaluatorId(rs.getLong("evaluator_id"))
        .evaluatorName(rs.getString("evaluator_name"))
        .content(rs.getString("content"))
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .build();
  }

}
