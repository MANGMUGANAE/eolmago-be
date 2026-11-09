package com.mangmuganae.eolmago.domain.training.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class HotdealPostRowMapper implements RowMapper<HotdealPost> {

  @Override
  public HotdealPost mapRow(ResultSet rs, int rowNum) throws SQLException {
    int finalLabel = rs.getInt("final_label");
    return HotdealPost.builder()
        .id(rs.getLong("id"))
        .title(rs.getString("title"))
        .content(rs.getString("content"))
        .contentHash(rs.getString("content_hash"))
        .status(rs.getString("status"))
        .finalLabel(rs.wasNull() ? null : finalLabel)
        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
        .build();
  }

}
