package com.eguller.hntoplinks.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CheckPointRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;
  public CheckPointRepository(NamedParameterJdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
  }

  public Long getLastItem() {
    var lastItem = jdbcTemplate.queryForObject("""
      SELECT
        checkpoint
      FROM
        checkpoints
      WHERE
        name='stories'
      """, Map.ofEntries(), Long.class);
    return lastItem;
  }

  public void saveStoriesCheckPoint(Long checkpoint) {
    jdbcTemplate.update("""
        UPDATE
          checkpoints
        SET
          checkpoint = :checkpoint
        WHERE
          name='stories'
        """, Map.ofEntries(
          Map.entry("checkpoint", checkpoint)
        ));
  }
}
